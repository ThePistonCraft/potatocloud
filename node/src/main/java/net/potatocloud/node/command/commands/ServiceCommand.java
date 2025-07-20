package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.node.Node;
import net.potatocloud.node.command.Command;
import net.potatocloud.node.command.TabCompleter;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.screen.Screen;
import net.potatocloud.node.service.ServiceImpl;
import net.potatocloud.node.utils.DurationUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class ServiceCommand implements Command, TabCompleter {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private final Logger logger;
    private final ServiceManager serviceManager;
    private final ServiceGroupManager groupManager;

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            sendHelp();
            return;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> listServices();
            case "start" -> startService(args);
            case "stop" -> stopService(args);
            case "info" -> infoService(args);
            case "execute" -> executeService(args);
            case "logs" -> logsService(args);
            case "screen" -> screenService(args);
            case "edit" -> editService(args);
            case "property" -> propertyService(args);
            case "copy" -> copyService(args);
            default -> sendHelp();
        }
    }

    private void listServices() {
        final List<Service> services = serviceManager.getAllServices();
        if (services.isEmpty()) {
            logger.info("There are &cno &7running services");
            return;
        }
        for (final Service service : services) {
            logger.info("&8» &a" + service.getName() + " &7- Group: &a" + service.getServiceGroup().getName() + " &7- Status: &a" + service.getStatus());
        }
    }

    private void startService(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7service start &8[&agroupName&8] (&aamount&8)");
            return;
        }

        final String groupName = args[1];
        if (!groupManager.existsServiceGroup(groupName)) {
            logger.info("&cNo service group found with the name &a" + groupName);
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount <= 0) {
                    logger.info("&cUsage&8: &7service start &8[&agroupName&8] (&aamount&8)");
                    return;
                }
            } catch (NumberFormatException e) {
                logger.info("&cUsage&8: &7service start &8[&agroupName&8] (&aamount&8)");
                return;
            }
        }

        final ServiceGroup group = groupManager.getServiceGroup(groupName);
        serviceManager.startServices(group, amount);

        logger.info("&7Starting " + amount + " new service(s) of group &a" + groupName);
    }

    private void stopService(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7service stop &8[&aname&8]");
            return;
        }
        final String name = args[1];
        final Service service = serviceManager.getService(name);
        if (service == null) {
            logger.info("&cNo service found with the name &a" + name);
            return;
        }
        service.shutdown();
    }

    private void infoService(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7service info &8[&aname&8]");
            return;
        }
        final String name = args[1];
        final Service service = serviceManager.getService(name);
        if (service == null) {
            logger.info("&cNo service found with the name &a" + name);
            return;
        }
        logger.info("Name: &a" + service.getName());
        logger.info("Group: &a" + service.getServiceGroup().getName());
        logger.info("Port: &a" + service.getPort());
        logger.info("Status: &a" + service.getStatus());
        logger.info("Online Players: &a" + service.getOnlinePlayers());
        logger.info("Max Players: &a" + service.getMaxPlayers());
        logger.info("Memory usage: &a" + service.getUsedMemory() + "MB");
        logger.info("Start Timestamp: &a" + TIME_FORMAT.format(service.getStartTimestamp()));
        logger.info("Online Time: &a" + DurationUtil.formatDuration(System.currentTimeMillis() - service.getStartTimestamp()));
    }

    private void executeService(String args[]) {
        if (args.length < 3) {
            logger.info("&cUsage&8: &7service execute &8[&aname&8] [&acommand&8...]");
            return;
        }
        final String name = args[1];
        final Service service = serviceManager.getService(name);
        if (service == null) {
            logger.info("&cNo service found with the name &a" + name);
            return;
        }
        if (!service.isOnline()) {
            logger.info("Service &a" + name + " &7is &cno &7online");
            return;
        }

        final String commandToExecute = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if (service.executeCommand(commandToExecute)) {
            logger.info("&7Executed command on service &a" + name + "&8: &7" + commandToExecute);
        } else {
            logger.info("&cFailed to execute command on service &a" + name);
        }
    }

    private void logsService(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7service logs &8[&aname&8]");
            return;
        }
        final String name = args[1];
        final Service service = serviceManager.getService(name);
        if (service == null) {
            logger.info("&cNo service found with the name &a" + name);
            return;
        }

        List<String> logs = ((ServiceImpl) service).getLogs();
        if (logs.isEmpty()) {
            logger.info("No logs found for service &a" + name);
        } else {
            logger.info("Logs for service &a" + name + ":");
            for (String logLine : logs) {
                logger.info(logLine);
            }
        }
    }

    private void screenService(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7service screen &8[&aname&8]");
            return;
        }
        final String name = args[1];
        final Service service = serviceManager.getService(name);
        if (service == null) {
            logger.info("&cNo service found with the name &a" + name);
            return;
        }
        if (service instanceof ServiceImpl impl) {
            Screen screen = Node.getInstance().getScreenManager().getScreen(impl.getScreen().getName());
            if (screen == null) {
                logger.error("Cant switch to screen of service " + name);
                return;
            }
            Node.getInstance().getScreenManager().switchScreen(screen.getName());
        }
    }

    private void editService(String[] args) {
        if (args.length < 4) {
            logger.info("&cUsage&8: &7service edit &8[&aname&8] [&akey&8] [&avalue&8]");
            return;
        }

        final String name = args[1];
        final Service service = serviceManager.getService(name);
        if (service == null) {
            logger.info("&cNo service found with the name &a" + name);
            return;
        }

        final String key = args[2].toLowerCase();
        final String value = args[3];

        try {
            switch (key) {
                case "maxplayers" -> service.setMaxPlayers(Integer.parseInt(value));

                default -> {
                    logger.info("&cUsage&8: &7service edit &8[&aname&8] [&akey&8] [&avalue&8]");
                    return;
                }
            }
            service.update();
            logger.info("Updated &a" + key + " &7for service &a" + name + "&7 to &a" + value);
        } catch (NumberFormatException ex) {
            logger.info("&cUsage&8: &7service edit &8[&aname&8] [&akey&8] [&avalue&8]");
        }
    }

    private void propertyService(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7service property &8[&7list&8|&7set&8|&7remove&8] [&aname&8] [&akey&8] [&avalue&8]");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "list" -> {
                if (args.length < 3) {
                    logger.info("&cUsage&8: &7service property list &8[&aname&8]");
                    return;
                }

                final String name = args[2];
                final Service service = serviceManager.getService(name);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + name);
                    return;
                }

                final Set<Property> properties = service.getProperties();

                if (properties.isEmpty()) {
                    logger.info("No properties found for service &a" + name);
                    return;
                }

                logger.info("Properties of service &a" + name + "&8:");
                for (Property property : properties) {
                    logger.info("&8» &a" + property.getName() + " &7- " + property.getValue());
                }
            }
            case "remove" -> {
                if (args.length < 4) {
                    logger.info("&cUsage&8: &7service property remove &8[&aname&8] [&akey&8]");
                    return;
                }

                final String name = args[2];
                final Service service = serviceManager.getService(name);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + name);
                    return;
                }

                final String key = args[3].toLowerCase();
                final Property property = service.getProperty(key);
                if (property == null) {
                    logger.info("Property &a" + key + "&7 was &cnot found &7in service &a" + name);
                    return;
                }
                service.getProperties().remove(property);
                service.update();
                logger.info("Property &a" + key + " &7was removed in service &a" + name);
            }
            case "set" -> {
                if (args.length < 4) {
                    logger.info("&cUsage&8: &7service property set &8[&aname&8] [&akey&8] [&avalue&8]");
                    return;
                }

                final String name = args[2];
                final Service service = serviceManager.getService(name);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + name);
                    return;
                }

                final String key = args[3].toLowerCase();

                // check if the property the user wants to add is a default property
                final Property defaultProperty = Property.getDefaultProperties().stream()
                        .filter(p -> p.getName().equalsIgnoreCase(key))
                        .findFirst()
                        .orElse(null);

                // set default property
                if (defaultProperty != null) {
                    service.setProperty(new Property(defaultProperty.getName(), defaultProperty.getDefaultValue()));
                    service.update();
                    logger.info("Default Property &a" + key + " &7was set to &a" + defaultProperty.getDefaultValue() + " &7in service &a" + name);
                    return;
                }

                if (args.length < 5) {
                    logger.info("&cUsage&8: &7service property set &8[&aname&8] [&akey&8] [&avalue&8]");
                    return;
                }

                //set custom property
                try {
                    final String value = args[4];
                    service.setProperty(new Property(key, value));
                    service.update();
                    logger.info("Custom Property &a" + key + " &7was set to &a" + value + " &7in service &a" + name);
                } catch (Exception e) {
                    logger.info("&cUsage&8: &7service property set &8[&aname&8] [&akey&8] [&avalue&8]");
                }
            }
            default ->
                    logger.info("&cUsage&8: &7service property &8[&7list&8|&7set&8|&7remove&8] [&aname&8] [&akey&8] [&avalue&8]");
        }
    }

    private void copyService(String[] args) {
        if (args.length < 3) {
            logger.info("&cUsage&8: &7service copy &8[&aname&8] [&atemplate&8] (&afilter&8)");
            return;
        }

        final String name = args[1];
        final Service service = serviceManager.getService(name);
        if (service == null) {
            logger.info("&cNo service found with the name &a" + name);
            return;
        }

        final String template = args[2];
        String filter;
        if (args.length >= 4) {
            filter = args[3];
        } else {
            filter = "";
        }


        if (filter.isEmpty()) {
            service.copy(template);
        } else {
            service.copy(template, filter);
        }

        logger.info("Copied &a" + (filter.isEmpty() ? "all service files" : filter) + " &7to template: &a" + template);
    }


    private void sendHelp() {
        logger.info("service list &8- &7List all running services");
        logger.info("service start &8[&agroupName&8] (&aamount&8) - &7Start new service(s)");
        logger.info("service stop &8[&aname&8] - &7Stop a running service");
        logger.info("service info &8[&aname&8] - &7Show details of a service");
        logger.info("service execute &8[&aname&8] [&acommand&8...] - &7Execute a command on a service");
        logger.info("service logs &8[&aname&8] - &7Print all logs of a service");
        logger.info("service screen &8[&aname&8] - &7Enter the screen of a service");
        logger.info("service edit &8[&aname&8] [&akey&8] [&avalue&8] - &7Edit a service");
        logger.info("service property &8[&7list&8|&7set&8|&7remove&8] [&aname&8] [&akey&8] [&avalue&8] - &7Manage properties of a service");
        logger.info("service copy &8[&aname&8] [&atemplate&8] (&afilter&8) - &7Copy files from a service to a template");
    }

    @Override
    public String getName() {
        return "service";
    }

    @Override
    public String getDescription() {
        return "Manage services";
    }

    @Override
    public List<String> getAliases() {
        return List.of("services", "ser");
    }

    @Override
    public List<String> complete(String[] args) {
        if (args.length == 1) {
            return List.of("list", "start", "stop", "info", "execute", "logs", "screen", "edit", "property", "copy").stream()
                    .filter(input -> input.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        String sub = args[0].toLowerCase();

        if (sub.equalsIgnoreCase("start") || sub.equalsIgnoreCase("stop") || sub.equalsIgnoreCase("info")
                || sub.equalsIgnoreCase("execute") || sub.equalsIgnoreCase("logs") || sub.equalsIgnoreCase("screen") ||
                sub.equalsIgnoreCase("edit") || sub.equalsIgnoreCase("copy")) {
            if (args.length == 2) {
                return serviceManager.getAllServices().stream().map(Service::getName)
                        .toList().stream().filter(input -> input.startsWith(args[1])).toList();
            }
        }

        if (sub.equals("edit") && args.length == 3) {
            return List.of("maxPlayers")
                    .stream()
                    .filter(key -> key.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        if (sub.equals("property")) {
            if (args.length == 2) {
                return List.of("list", "set", "remove").stream()
                        .filter(s -> s.startsWith(args[1].toLowerCase()))
                        .toList();
            }

            if (args.length == 3) {
                return serviceManager.getAllServices().stream().map(Service::getName)
                        .toList().stream().filter(input -> input.startsWith(args[2])).toList();
            }

            if (args.length == 4 && args[1].equalsIgnoreCase("remove")) {
                final String name = args[2];
                if (serviceManager.getService(name) != null) {
                    return serviceManager.getService(name).getProperties().stream()
                            .map(Property::getName)
                            .filter(p -> p.startsWith(args[3]))
                            .toList();
                }
            }

            if (args.length == 4 && args[1].equalsIgnoreCase("set")) {
                List<String> completions = new ArrayList<>();
                completions.add("<custom>");
                completions.addAll(Property.getDefaultProperties().stream()
                        .map(Property::getName)
                        .filter(s -> s.startsWith(args[3].toLowerCase()))
                        .toList());
                return completions;
            }
        }

        return List.of();
    }
}