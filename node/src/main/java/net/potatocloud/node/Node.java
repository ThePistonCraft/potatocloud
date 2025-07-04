package net.potatocloud.node;

import lombok.Getter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.console.ExceptionMessageHandler;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.group.ServiceGroupManagerImpl;

import java.io.File;
import java.nio.file.Path;

@Getter
public class Node extends CloudAPI {

    private final NodeConfig config;
    private final CommandManager commandManager;
    private final Console console;
    private final Logger logger;
    private final ServiceGroupManager groupManager;

    public Node() {
        config = new NodeConfig();
        commandManager = new CommandManager();
        console = new Console(config.getPrompt(), commandManager, this);
        console.start();

        logger = new Logger(console, new File(config.getLogsFolder()));
        new ExceptionMessageHandler(logger);

        groupManager = new ServiceGroupManagerImpl(Path.of(config.getGroupsFolder()));
        logger.info("Found &a" + groupManager.getAllServiceGroups().size() + "&7 Service Groups!");
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
        return null;
    }

    public void shutdown() {
        logger.info("&cShutting down node...");
        console.close();
    }
}
