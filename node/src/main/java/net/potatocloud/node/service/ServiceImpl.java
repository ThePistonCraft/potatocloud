package net.potatocloud.node.service;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.impl.PandaSpigotVersion;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.core.networking.packets.service.ServiceRemovePacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Logger;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class ServiceImpl implements Service {

    private final int serviceId;
    private final int port;
    private final ServiceGroup serviceGroup;
    private final NodeConfig config;
    private final Logger logger;
    private final List<String> logs = new ArrayList<>();
    @Setter
    private int maxPlayers;
    @Setter
    private ServiceState state = ServiceState.STOPPED;
    private long startTimestamp;
    private Path directory;
    private Process serverProcess;
    private BufferedWriter processWriter;
    private BufferedReader processReader;

    public ServiceImpl(int serviceId, int port, ServiceGroup serviceGroup, NodeConfig config, Logger logger) {
        this.serviceId = serviceId;
        this.port = port;
        this.serviceGroup = serviceGroup;
        this.config = config;
        this.logger = logger;

        maxPlayers = serviceGroup.getMaxPlayers();
    }

    @Override
    public String getName() {
        return serviceGroup.getName() + config.getSplitter() + serviceId;
    }

    public boolean isOnline() {
        return state == ServiceState.RUNNING;
    }

    public int getUsedMemory() {
        // todo
        return 0;
    }

    @Override
    public int getPort() {
        return port;
    }

    public int getOnlinePlayers() {
        // todo
        return 0;
    }

    @SneakyThrows
    public void start() {
        if (isOnline()) {
            return;
        }

        state = ServiceState.STARTING;
        startTimestamp = System.currentTimeMillis();

        // create service folder
        final Path staticFolder = Path.of(config.getStaticFolder());
        final Path tempFolder = Path.of(config.getTempServicesFolder());
        directory = serviceGroup.isStatic() ? staticFolder.resolve(getName()) : tempFolder.resolve(getName());

        if (!serviceGroup.isStatic()) {
            FileUtils.deleteDirectory(directory.toFile());
        }

        Files.createDirectories(directory);

        // copy templates
        for (String templateName : serviceGroup.getServiceTemplates()) {
            Node.getInstance().getTemplateManager().copyTemplate(templateName, directory);
        }

        // copy cloud plugin into plugins folder
        final Path pluginsFolder = directory.resolve("plugins");
        Files.createDirectories(pluginsFolder);

        FileUtils.copyFile(Path.of(config.getDataFolder(), "potatocloud-plugin.jar").toFile(), pluginsFolder.resolve("potatocloud-plugin.jar").toFile());

        // download and configure the platform of the service
        final Platform platform = getServiceGroup().getPlatform();
        Node.getInstance().getPlatformManager().downloadPlatform(platform);

        if (!platform.isProxy()) {
            final Properties properties = new Properties();
            final File file = directory.resolve("server.properties").toFile();

            if (!file.exists()) {
                properties.load(Files.newInputStream(Path.of(config.getDataFolder(), "server.properties")));
            } else {
                properties.load(new FileInputStream(file));
            }

            properties.setProperty("server-port", String.valueOf(port));
            properties.setProperty("query.port", String.valueOf(port));

            final Path serverPropertiesPath = directory.resolve("server.properties");
            properties.store(Files.newOutputStream(serverPropertiesPath), null);

            // spigot config
            final Path spigotConfigFile = directory.resolve("spigot.yml");

            if (!Files.exists(spigotConfigFile)) {
                Files.copy(Path.of(config.getDataFolder(), "spigot.yml"), spigotConfigFile);
            }

        } else {
            final Path velocityConfigFile = directory.resolve("velocity.toml");

            if (!Files.exists(velocityConfigFile)) {
                Files.copy(Path.of(config.getDataFolder(), "velocity.toml"), velocityConfigFile);
            }

            String fileContent = Files.readString(velocityConfigFile);
            fileContent = fileContent.replace(
                    "bind = \"0.0.0.0:25565\"",
                    "bind = \"0.0.0.0:" + port + "\""
            );

            Files.writeString(velocityConfigFile, fileContent);

            // a forwarding secret file has to be created or else velocity will throw an error
            Files.writeString(directory.resolve("forwarding.secret"), UUID.randomUUID().toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }

        final Path jarPath = Path.of(config.getPlatformsFolder())
                .resolve(platform.getFullName())
                .resolve(platform.getFullName() + ".jar");

        final Path serverJarPath = directory.resolve("server.jar");
        FileUtils.copyFile(jarPath.toFile(), serverJarPath.toFile());

        // create start arguments
        final ArrayList<String> args = new ArrayList<>();
        args.add("java");
        args.add("-Xms" + serviceGroup.getMaxMemory() + "M");
        args.add("-Xmx" + serviceGroup.getMaxMemory() + "M");
        args.add("-Dpotatocloud.service.name=" + getName());

        if (!platform.isProxy()) {
            args.add("-Dcom.mojang.eula.agree=true");
        }

        if (!platform.getRecommendedFlags().isEmpty()) {
            args.addAll(platform.getRecommendedFlags());
        }

        args.add("-jar");
        args.add(serverJarPath.toAbsolutePath().toString());

        if (!platform.isProxy() && !(platform instanceof PandaSpigotVersion)) {
            args.add("-nogui");
        }

        // crate and start the service process
        final ProcessBuilder processBuilder = new ProcessBuilder(args).directory(directory.toFile());
        serverProcess = processBuilder.start();

        processWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
        processReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));

        new Thread(() -> {
            try {
                String line;
                while ((line = processReader.readLine()) != null) {
                    logs.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "processReader-" + getName()).start();

        logger.info("The Service &a" + getName() + "&7 is now starting... &8[&7Port&8: &a" + port + ", &7Group&8: &a" + serviceGroup.getName() + "&8]");
    }

    @Override
    public void shutdown() {
        if (state == ServiceState.STOPPED || state == ServiceState.STOPPING) {
            return;
        }
        new Thread(this::shutdownBlocking, "Shutdown-" + getName()).start();
    }

    @SneakyThrows
    public void shutdownBlocking() {
        if (state == ServiceState.STOPPED || state == ServiceState.STOPPING) {
            return;
        }

        logger.info("Stopping service &a" + getName() + "&7...");
        state = ServiceState.STOPPING;

        executeCommand(serviceGroup.getPlatform().isProxy() ? "end" : "stop");

        if (processWriter != null) {
            processWriter.close();
            processWriter = null;
        }

        if (serverProcess != null) {
            boolean finished = serverProcess.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                serverProcess.toHandle().destroyForcibly();
                serverProcess.waitFor();
            }
            serverProcess = null;
        }

        startTimestamp = 0L;

        ((ServiceManagerImpl) Node.getInstance().getServiceManager()).removeService(this);

        if (Node.getInstance().getServer() != null) {
            Node.getInstance().getServer().broadcastPacket(new ServiceRemovePacket(this.getName()));
        }

        if (!serviceGroup.isStatic()) {
            try {
                FileUtils.deleteDirectory(directory.toFile());
            } catch (IOException e) {
                logger.error("The temp directory for " + getName() + " could not be deleted! The service might still be running");
            }
        }

        state = ServiceState.STOPPED;
        logger.info("The Service &a" + getName() + " &7has been stopped.");
    }


    @Override
    @SneakyThrows
    public boolean executeCommand(String command) {
        if (serverProcess == null || !serverProcess.isAlive() || processWriter == null) {
            return false;
        }
        processWriter.write(command);
        processWriter.newLine();
        processWriter.flush();
        return true;
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }

    @SneakyThrows
    public List<String> getLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }
}
