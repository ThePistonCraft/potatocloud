package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersions;
import net.potatocloud.node.command.Command;
import net.potatocloud.node.console.Logger;

import java.util.List;

@RequiredArgsConstructor
public class GroupCommand implements Command {

    private final Logger logger;
    private final ServiceGroupManager groupManager;

    @Override
    public void execute(final String[] args) {
        if (args.length == 0) {
            sendHelp();
            return;
        }

        final String sub = args[0].toLowerCase();

        switch (sub) {
            case "list" -> listGroups();
            case "create" -> createGroup(args);
            case "delete" -> deleteGroup(args);
            case "info" -> infoGroup(args);
            default -> sendHelp();
        }
    }

    private void listGroups() {
        final List<ServiceGroup> groups = groupManager.getAllServiceGroups();
        if (groups.isEmpty()) {
            logger.info("There are &cno &7service groups");
            return;
        }
        for (final ServiceGroup group : groups) {
            logger.info("&8» &a" + group.getName());
        }
    }

    private void createGroup(final String[] args) {
        if (args.length < 9) {
            logger.info("&cUsage&8: &7group create &8[&aname&8, &aminOnlineCount&8, &amaxOnlineCount&8, &amaxPlayers&8, &amaxMemory&8, &afallback&8, &astatic&8, &aplatformName&8]");
            return;
        }

        final String name = args[1];
        if (groupManager.existsServiceGroup(name)) {
            logger.info("&7A service group with the name &a" + name + " &calready exists");
            return;
        }

        try {

            final String platformName = args[8];
            final Platform platform = PlatformVersions.getPlatformByName(platformName);
            if (platform == null) {
                logger.info("&cThis platform does not exist!");
                return;
            }

            groupManager.createServiceGroup(
                    name,
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]),
                    Integer.parseInt(args[5]),
                    Boolean.parseBoolean(args[6]),
                    Boolean.parseBoolean(args[7]),
                    platform
            );

            logger.info("&7Service group &a" + name + " &7was created &asuccessfully");
        } catch (final NumberFormatException e) {
            logger.info("&cUsage&8: &7group create &8[&aname&8, &aminOnlineCount&8, &amaxOnlineCount&8, &amaxPlayers&8, &amaxMemory&8, &afallback&8, &astatic&8, &aplatformName&8]");
        }
    }

    private void deleteGroup(final String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7group delete &8[&aname&8]");
            return;
        }
        final String name = args[1];
        if (!groupManager.existsServiceGroup(name)) {
            logger.info("&cNo service group found with the name &a" + name);
            return;
        }
        final ServiceGroup group = groupManager.getServiceGroup(name);
        groupManager.deleteServiceGroup(group);
        logger.info("&aService group &a" + name + " &awas deleted");
    }

    private void infoGroup(final String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7group info &8[&aname&8]");
            return;
        }
        final String name = args[1];
        if (!groupManager.existsServiceGroup(name)) {
            logger.info("&cNo service group found with the name &a" + name);
            return;
        }
        final ServiceGroup group = groupManager.getServiceGroup(name);
        logger.info("Name: &a" + group.getName());
        logger.info("Min Online Count: &a" + group.getMinOnlineCount());
        logger.info("Max Online Count: &a" + group.getMaxOnlineCount());
        logger.info("Max Players: &a" + group.getMaxPlayers());
        logger.info("Max Memory: &a" + group.getMaxMemory() + "MB");
        logger.info("Fallback: &a" + group.isFallback());
        logger.info("Static: &a" + group.isStatic());
        logger.info("Platform: &a" + group.getPlatform().getFullName());
        logger.info("Templates: &a" + String.join(", ", group.getServiceTemplates()));
    }

    private void sendHelp() {
        logger.info("group create &8[&aname&8, &aminOnlineCount&8, &amaxOnlineCount&8, &amaxPlayers&8, &amaxMemory&8, &afallback&8, &astatic&8, &aplatformName&8] - &7Create a new service group");
        logger.info("group delete &8[&aname&8] - &7Delete a service group");
        logger.info("group list &8- &7List all service groups");
        logger.info("group info &8[&aname&8] - &7Show details of a service group");
    }

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public String getDescription() {
        return "Manage service groups";
    }

    @Override
    public List<String> getAliases() {
        return List.of("groups");
    }

    @Override
    public List<String> complete(String[] args) {
        return List.of("");
    }

}
