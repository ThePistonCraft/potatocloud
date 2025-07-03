package net.potatocloud.manager;

import lombok.Getter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.command.CommandManager;
import net.potatocloud.core.console.Console;
import net.potatocloud.core.console.ExceptionMessageHandler;
import net.potatocloud.core.console.Logger;
import net.potatocloud.core.shutdown.ShutdownHandler;
import net.potatocloud.manager.config.ManagerConfig;
import net.potatocloud.manager.group.ServiceGroupManagerImpl;

import java.io.File;
import java.nio.file.Path;

@Getter
public class Manager extends CloudAPI implements ShutdownHandler {

    private final ManagerConfig config;
    private final CommandManager commandManager;
    private final Console console;
    private final Logger logger;
    private final ServiceGroupManager groupManager;

    public Manager() {
        config = new ManagerConfig();
        commandManager = new CommandManager();
        console = new Console(config.getPrompt(), commandManager, this);
        console.start();

        logger = new Logger(console, new File(config.getLogsFolder()));
        new ExceptionMessageHandler(logger);

        groupManager = new ServiceGroupManagerImpl(Path.of(config.getGroupsFolder()));
        logger.info("Found &a" + groupManager.getAllServiceGroups().size() + "&7 Service Groups!");
    }

    public static Manager getInstance() {
        return (Manager) CloudAPI.getInstance();
    }

    @Override
    public ServiceGroupManager getServiceGroupManager() {
        return groupManager;
    }

    @Override
    public ServiceManager getServiceManager() {
        return null;
    }

    @Override
    public void shutdown() {
        logger.info("&cShutting down cloud manager...");
        console.close();
    }
}
