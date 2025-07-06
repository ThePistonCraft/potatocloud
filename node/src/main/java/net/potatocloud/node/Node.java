package net.potatocloud.node;

import lombok.Getter;
import lombok.SneakyThrows;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.node.command.CommandManager;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Console;
import net.potatocloud.node.console.ExceptionMessageHandler;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.platform.PlatformDownloader;
import net.potatocloud.node.service.ServiceManagerImpl;
import net.potatocloud.node.service.ServiceStartQueue;
import net.potatocloud.node.template.TemplateManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;

@Getter
public class Node extends CloudAPI {

    private final NodeConfig config;
    private final CommandManager commandManager;
    private final Console console;
    private final Logger logger;
    private final TemplateManager templateManager;
    private final ServiceGroupManager groupManager;
    private final PlatformDownloader platformDownloader;
    private final ServiceManagerImpl serviceManager;
    private final ServiceStartQueue serviceStartQueue;

    public Node() {
        config = new NodeConfig();
        commandManager = new CommandManager();
        console = new Console(config.getPrompt(), commandManager, this);
        console.start();

        logger = new Logger(console, new File(config.getLogsFolder()));
        new ExceptionMessageHandler(logger);

        templateManager = new TemplateManager(logger, Path.of(config.getTemplatesFolder()));
        groupManager = new ServiceGroupManagerImpl(Path.of(config.getGroupsFolder()));
        logger.info("Found &a" + groupManager.getAllServiceGroups().size() + "&7 Service Groups!");

        platformDownloader = new PlatformDownloader(Path.of(config.getPlatformsFolder()), logger);
        serviceManager = new ServiceManagerImpl(config);
        serviceStartQueue = new ServiceStartQueue();
        serviceStartQueue.start();
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

    @SneakyThrows
    public void shutdown() {
        logger.info("&cShutting down node...");
        serviceStartQueue.close();

        for (Service service : serviceManager.getAllOnlineServices()) {
            service.shutdown();
        }

        FileUtils.deleteDirectory(Path.of(config.getTempServicesFolder()).toFile());
        console.close();
    }
}
