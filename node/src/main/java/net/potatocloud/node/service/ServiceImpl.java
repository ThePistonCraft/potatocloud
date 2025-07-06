package net.potatocloud.node.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.impl.PaperMCPlatformVersion;
import net.potatocloud.api.platform.impl.PurpurPlatformVersion;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.node.Node;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Logger;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public class ServiceImpl implements Service {

    private final int serviceId;
    private final int port;
    private final ServiceGroup serviceGroup;
    private final NodeConfig config;
    private final Logger logger;

    private ServiceState state = ServiceState.STOPPED;
    private long startTimestamp;
    private Path directory;
    private Process serverProcess;

    private BufferedWriter processWriter;

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
    public String getHost() {
        // todo
        return "";
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

        // download and configure the platform of the service
        final Platform platform = getServiceGroup().getPlatform();
        Node.getInstance().getPlatformDownloader().download(platform);

        if (!platform.isProxy()) {
            final Properties properties = new Properties();
            final File file = directory.resolve("server.properties").toFile();

            if (!file.exists()) {
                properties.load(getClass().getResourceAsStream("/default-files/server.properties"));
            } else {
                properties.load(new FileInputStream(file));
            }

            properties.setProperty("server-port", String.valueOf(port));
            properties.setProperty("query.port", String.valueOf(port));

            final Path serverPropertiesPath = directory.resolve("server.properties");
            properties.store(Files.newOutputStream(serverPropertiesPath), null);

        } else {
            // velocity

            Path velocityConfigFile = directory.resolve("velocity.toml");
            if (!velocityConfigFile.toFile().exists()) {
                velocityConfigFile = Path.of(getClass().getResourceAsStream("/default-files/velocity.toml").toString());
            }

            String fileContent = Files.readString(velocityConfigFile);
            fileContent = fileContent.replace("bind = \"0.0.0.0:25565\"", "bind = \"0.0.0.0:" + port + "\"");

            Files.writeString(velocityConfigFile, fileContent);
        }

        final Path jarPath = Path.of(config.getPlatformsFolder()).resolve(platform.getFullName() + ".jar");
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

        if (platform instanceof PaperMCPlatformVersion || platform instanceof PurpurPlatformVersion) {
            args.add("-nogui");
        }

        // crate and start the service process
        final ProcessBuilder processBuilder = new ProcessBuilder(args).directory(directory.toFile());
        serverProcess = processBuilder.start();

        processWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));

        logger.info("Service &a" + getName() + "&7 is now starting... &8 [&7Port&8: &a" + port + ", &7Group&8: &a" + serviceGroup.getName() + "&8]");
    }

    public void shutdownInBackground() {
        if (state == ServiceState.STOPPED || state == ServiceState.STOPPING) {
            return;
        }
        new Thread(this::shutdown, "Shutdown-" + getName()).start();
    }

    @Override
    @SneakyThrows
    public void shutdown() {
        if (state == ServiceState.STOPPED || state == ServiceState.STOPPING) {
            return;
        }

        logger.info("Stopping service &9" + getName() + "&7...");

        state = ServiceState.STOPPING;

        executeCommand(serviceGroup.getPlatform().isProxy() ? "end" : "stop");

        if (processWriter != null) {
            processWriter.close();
            processWriter = null;
        }

        if (serverProcess.waitFor(10, TimeUnit.SECONDS)) {
            serverProcess.toHandle().destroyForcibly();
            serverProcess = null;
            return;
        }

        state = ServiceState.STOPPED;
        startTimestamp = 0L;

        ((ServiceManagerImpl) Node.getInstance().getServiceManager()).removeService(this);

        if (!serviceGroup.isStatic()) {
            FileUtils.deleteDirectory(directory.toFile());
        }

        logger.info("Service &9" + getName() + " &7has been stopped.");
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
}
