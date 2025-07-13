package net.potatocloud.node;

import lombok.Getter;
import lombok.SneakyThrows;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.event.ServerEventManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketManager;
import net.potatocloud.core.networking.netty.NettyNetworkServer;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.command.commands.*;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.console.ExceptionMessageHandler;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.platform.PlatformManager;
import net.potatocloud.node.service.ServiceManagerImpl;
import net.potatocloud.node.service.ServiceStartQueue;
import net.potatocloud.node.template.TemplateManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Getter
public class Node extends CloudAPI {

    private final NodeConfig config;
    private final PacketManager packetManager;
    private final NetworkServer server;
    private final EventManager eventManager;
    private final CommandManager commandManager;
    private final Console console;
    private final Logger logger;
    private final TemplateManager templateManager;
    private final ServiceGroupManager groupManager;
    private final PlatformManager platformManager;
    private final ServiceManagerImpl serviceManager;
    private final ServiceStartQueue serviceStartQueue;

    public Node() {
        config = new NodeConfig();
        try {
            FileUtils.deleteDirectory(new File(config.getTempServicesFolder()));
        } catch (IOException ignored) {

        }

        packetManager = new PacketManager();
        server = new NettyNetworkServer(packetManager);
        eventManager = new ServerEventManager(server);

        server.start(config.getNodeHost(), config.getNodePort());

        commandManager = new CommandManager();
        console = new Console(config.getPrompt(), commandManager, this);
        console.start();

        logger = new Logger(console, new File(config.getLogsFolder()));
        new ExceptionMessageHandler(logger);

        templateManager = new TemplateManager(logger, Path.of(config.getTemplatesFolder()));
        groupManager = new ServiceGroupManagerImpl(Path.of(config.getGroupsFolder()));

        ((ServiceGroupManagerImpl) groupManager).loadGroups();
        logger.info("Found &a" + groupManager.getAllServiceGroups().size() + "&7 Service Groups!");

        platformManager = new PlatformManager(Path.of(config.getPlatformsFolder()), logger);
        serviceManager = new ServiceManagerImpl(config, logger);
        serviceManager.getAllOnlineServices().clear();

        registerCommands();

        logger.info("Successfully started the potatocloud node &8(&7Took &a"+ (System.currentTimeMillis() - Long.parseLong(System.getProperty("nodeStartupTime"))) + "ms&8)");

        serviceStartQueue = new ServiceStartQueue();
        serviceStartQueue.start();
    }

    private void registerCommands() {
        commandManager.registerCommand(new GroupCommand(logger, groupManager));
        commandManager.registerCommand(new ServiceCommand(logger, serviceManager, groupManager));
        commandManager.registerCommand(new ShutdownCommand(this));
        commandManager.registerCommand(new PlatformCommand(logger, Path.of(config.getPlatformsFolder()), platformManager));
        commandManager.registerCommand(new ClearCommand(console));
        commandManager.registerCommand(new HelpCommand(logger, commandManager));
    }

    public static Node getInstance() {
        return (Node) CloudAPI.getInstance();
    }

    @Override
    public ServiceGroupManager getServiceGroupManager() {
        return groupManager;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @SneakyThrows
    public void shutdown() {
        logger.info("&cShutting down node...");
        serviceStartQueue.close();

        for (Service service : serviceManager.getAllOnlineServices()) {
            service.shutdown();
        }

        for (NetworkConnection connectedSession : server.getConnectedSessions()) {
            connectedSession.close();
        }
        server.shutdown();


        FileUtils.deleteDirectory(Path.of(config.getTempServicesFolder()).toFile());

        console.close();
    }
}
