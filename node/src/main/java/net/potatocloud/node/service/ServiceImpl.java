package net.potatocloud.node.service;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.event.events.service.PreparedServiceStartingEvent;
import net.potatocloud.api.event.events.service.ServiceStoppedEvent;
import net.potatocloud.api.event.events.service.ServiceStoppingEvent;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.impl.PandaSpigotVersion;
import net.potatocloud.api.platform.impl.PaperMCPlatformVersion;
import net.potatocloud.api.platform.impl.PurpurPlatformVersion;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.packets.service.ServiceRemovePacket;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.platform.PlatformManager;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.screen.ScreenManager;
import net.potatocloud.node.template.TemplateManager;
import org.apache.commons.io.FileUtils;
import oshi.SystemInfo;
import oshi.software.os.OSProcess;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class ServiceImpl implements Service {

    private final int serviceId;
    private final int port;
    private final ServiceGroup group;
    private final NodeConfig config;
    private final Logger logger;

    private final List<String> logs = new ArrayList<>();

    private final NetworkServer server;
    private final ScreenManager screenManager;
    private final TemplateManager templateManager;
    private final PlatformManager platformManager;
    private final EventManager eventManager;
    private final ServiceManager serviceManager;
    private final Console console;

    private final Set<Property> properties;
    private final Screen screen;

    @Setter
    private int maxPlayers;

    @Setter
    private ServiceStatus status = ServiceStatus.STOPPED;

    private long startTimestamp;

    private Path directory;

    private Process serverProcess;
    private BufferedWriter processWriter;
    private BufferedReader processReader;

    @Setter
    private ServiceProcessChecker processChecker;

    public ServiceImpl(
            int serviceId,
            int port,
            ServiceGroup group,
            NodeConfig config,
            Logger logger,
            NetworkServer server,
            ScreenManager screenManager,
            TemplateManager templateManager,
            PlatformManager platformManager,
            EventManager eventManager,
            ServiceManager serviceManager,
            Console console
    ) {
        this.serviceId = serviceId;
        this.port = port;
        this.group = group;
        this.config = config;
        this.logger = logger;
        this.server = server;
        this.screenManager = screenManager;
        this.templateManager = templateManager;
        this.platformManager = platformManager;
        this.eventManager = eventManager;
        this.serviceManager = serviceManager;
        this.console = console;

        maxPlayers = group.getMaxPlayers();
        properties = new HashSet<>(group.getProperties());

        screen = new Screen(getName());
        screenManager.addScreen(screen);
    }

    @Override
    public String getName() {
        return group.getName() + config.getSplitter() + serviceId;
    }

    public int getUsedMemory() {
        if (serverProcess == null || !serverProcess.isAlive()) {
            return 0;
        }

        final SystemInfo info = new SystemInfo();
        final OSProcess process = info.getOperatingSystem().getProcess((int) serverProcess.pid());

        if (process != null) {
            long usedBytes = process.getResidentSetSize();
            return (int) (usedBytes / 1024 / 1024);
        }
        return 0;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public ServiceGroup getServiceGroup() {
        return group;
    }

    @SneakyThrows
    public void start() {
        if (isOnline()) {
            return;
        }

        status = ServiceStatus.STARTING;
        startTimestamp = System.currentTimeMillis();

        // create service folder
        final Path staticFolder = Path.of(config.getStaticFolder());
        final Path tempFolder = Path.of(config.getTempServicesFolder());
        directory = group.isStatic() ? staticFolder.resolve(getName()) : tempFolder.resolve(getName());

        if (!group.isStatic()) {
            if (Files.exists(directory)) {
                FileUtils.deleteQuietly(directory.toFile());
            }
        }

        Files.createDirectories(directory);

        // copy templates
        for (String templateName : group.getServiceTemplates()) {
            templateManager.copyTemplate(templateName, directory);
        }

        // copy cloud plugin from data folder into server plugins folder
        final Path pluginsFolder = directory.resolve("plugins");
        Files.createDirectories(pluginsFolder);

        FileUtils.copyFile(Path.of(config.getDataFolder(), "potatocloud-plugin.jar").toFile(), pluginsFolder.resolve("potatocloud-plugin.jar").toFile());

        // download the platform of the service
        final Platform platform = group.getPlatform();
        platformManager.downloadPlatform(platform);

        // prepare the platform of the service
        if ((platform instanceof PaperMCPlatformVersion || platform instanceof PurpurPlatformVersion) && !platform.isProxy()) {
            // Paper and Purpur

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
        } else if (platform.isProxy() && platform instanceof PaperMCPlatformVersion) {
            // Velocity

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
            final Path forwardingSecretFile = directory.resolve("forwarding.secret");
            if (!Files.exists(forwardingSecretFile)) {
                Files.writeString(forwardingSecretFile, UUID.randomUUID().toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }

        // copy server file
        final Path platformFilePath = Path.of(config.getPlatformsFolder())
                .resolve(platform.getFullName())
                .resolve(platform.getFullName() + ".jar");

        final Path finalServerFilePath = directory.resolve("server.jar");
        FileUtils.copyFile(platformFilePath.toFile(), finalServerFilePath.toFile());

        // create start arguments
        final ArrayList<String> args = new ArrayList<>();
        args.add(getGroup().getJavaCommand());
        args.add("-Xms" + group.getMaxMemory() + "M");
        args.add("-Xmx" + group.getMaxMemory() + "M");
        args.add("-Dpotatocloud.service.name=" + getName());
        args.add("-Dpotatocloud.node.port=" + config.getNodePort());

        if (!platform.isProxy()) {
            args.add("-Dcom.mojang.eula.agree=true");
        }

        if (!platform.getRecommendedFlags().isEmpty()) {
            args.addAll(platform.getRecommendedFlags());
        }

        if (group.getCustomJvmFlags() != null) {
            args.addAll(group.getCustomJvmFlags());
        }

        args.add("-jar");
        args.add(platformFilePath.toAbsolutePath().toString());

        if (!platform.isProxy() && !(platform instanceof PandaSpigotVersion)) {
            args.add("-nogui");
        }

        // create and start the service process
        final ProcessBuilder processBuilder = new ProcessBuilder(args).directory(directory.toFile());
        serverProcess = processBuilder.start();

        processWriter = new BufferedWriter(new OutputStreamWriter(serverProcess.getOutputStream()));
        processReader = new BufferedReader(new InputStreamReader(serverProcess.getInputStream()));

        new Thread(() -> {
            try {
                String line;
                while ((line = processReader.readLine()) != null) {
                    logs.add(line);
                    screen.addLog(line);

                    if (screenManager.getCurrentScreen().getName().equals(getName())) {
                        console.println(line);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "ProcessReader-" + getName()).start();

        logger.info("Service &a" + this.getName() + "&7 is now starting... &8[&7Port&8: &a" + port + "&8, &7Group&8: &a" + group.getName() + "&8]");
        eventManager.call(new PreparedServiceStartingEvent(this.getName()));
    }

    @Override
    public void shutdown() {
        if (status == ServiceStatus.STOPPED || status == ServiceStatus.STOPPING) {
            return;
        }
        new Thread(this::shutdownBlocking, "Shutdown-" + getName()).start();
    }

    @SneakyThrows
    public void shutdownBlocking() {
        if (status == ServiceStatus.STOPPED || status == ServiceStatus.STOPPING) {
            return;
        }

        if (processChecker != null) {
            processChecker.interrupt();
            processChecker = null;
        }

        logger.info("Stopping service &a" + getName() + "&7...");
        status = ServiceStatus.STOPPING;

        if (server != null && eventManager != null) {
            eventManager.call(new ServiceStoppingEvent(this.getName()));
        }

        executeCommand(group.getPlatform().isProxy() ? "end" : "stop");

        if (processWriter != null) {
            processWriter.close();
            processWriter = null;
        }

        if (serverProcess != null) {
            final boolean finished = serverProcess.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                serverProcess.toHandle().destroyForcibly();
                serverProcess.waitFor();
            }
            serverProcess = null;
        }

        cleanup();
    }

    public void cleanup() {
        if (status == ServiceStatus.STOPPED) {
            return;
        }

        status = ServiceStatus.STOPPED;
        startTimestamp = 0L;

        ((ServiceManagerImpl) serviceManager).removeService(this);

        screenManager.removeScreen(screen);

        if (screenManager.getCurrentScreen().getName().equals(getName())) {
            screenManager.switchScreen(Screen.NODE_SCREEN);
        }

        if (server != null) {
            server.broadcastPacket(new ServiceRemovePacket(this.getName(), this.getPort()));

            eventManager.call(new ServiceStoppedEvent(this.getName()));
        }

        if (!group.isStatic()) {
            if (Files.exists(directory)) {
                if (!FileUtils.deleteQuietly(directory.toFile())) {
                    logger.error("Temp directory for " + getName() + " could not be deleted! The service might still be running");
                }
            }
        }

        logger.info("Service &a" + getName() + " &7has been stopped");
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
    @SneakyThrows
    public void copy(String template, String filter) {
        final Path templatesFolder = Path.of(config.getTemplatesFolder());
        Path targetPath = templatesFolder.resolve(template);
        Path sourcePath = directory;

        // a filter was set
        if (filter != null && filter.startsWith("/")) {
            // remove the / symbol
            sourcePath = directory.resolve(filter.substring(1));
            targetPath = targetPath.resolve(filter.substring(1));
        }

        if (!Files.exists(sourcePath)) {
            return;
        }

        if (!Files.exists(targetPath)) {
            templateManager.createTemplate(targetPath.toFile().getName());
        }

        try {
            FileUtils.copyDirectory(sourcePath.toFile(), targetPath.toFile());
        } catch (FileSystemException ignored) {

        }
    }

    @SneakyThrows
    public List<String> getLogs() {
        synchronized (logs) {
            return new ArrayList<>(logs);
        }
    }

    @Override
    public String getPropertyHolderName() {
        return getName();
    }
}
