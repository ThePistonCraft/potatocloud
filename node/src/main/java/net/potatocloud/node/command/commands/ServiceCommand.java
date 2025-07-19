package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
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
import java.util.Arrays;
import java.util.List;

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

        final String sub = args[0].toLowerCase();

        switch (sub) {
            case "list" -> {
                final List<Service> services = serviceManager.getAllServices();
                if (services.isEmpty()) {
                    logger.info("There are &cno &7running services");
                    return;
                }
                for (final Service service : services) {
                    logger.info("&8» &a" + service.getName() + " &7- Group: &a" + service.getServiceGroup().getName() + " &7- Status: &a" + service.getStatus());
                }
            }

            case "start" -> {
                if (args.length < 2) {
                    logger.info("&cUsage&8: &7service start &8[&agroupName&8] [&aamount&8]");
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
                            logger.info("&cUsage&8: &7service start &8[&agroupName&8] [&aamount&8]");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        logger.info("&cUsage&8: &7service start &8[&agroupName&8] [&aamount&8]");
                        return;
                    }
                }

                final ServiceGroup group = groupManager.getServiceGroup(groupName);
                serviceManager.startServices(group, amount);

                logger.info("&7Starting " + amount + " new service(s) of group &a" + groupName);
            }

            case "stop" -> {
                if (args.length < 2) {
                    logger.info("&cUsage&8: &7service stop &8[&aserviceName&8]");
                    return;
                }
                final String serviceName = args[1];
                final Service service = serviceManager.getService(serviceName);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + serviceName);
                    return;
                }
                service.shutdown();
            }

            case "info" -> {
                if (args.length < 2) {
                    logger.info("&cUsage&8: &7service info &8[&aserviceName&8]");
                    return;
                }
                final String serviceName = args[1];
                final Service service = serviceManager.getService(serviceName);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + serviceName);
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

            case "execute" -> {
                if (args.length < 3) {
                    logger.info("&cUsage&8: &7service execute &8[&aname&8] [&acommand&8...]");
                    return;
                }
                final String serviceName = args[1];
                final Service service = serviceManager.getService(serviceName);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + serviceName);
                    return;
                }
                if (!service.isOnline()) {
                    logger.info("Service &a" + serviceName + " &7is &cno &7online");
                    return;
                }

                final String commandToExecute = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

                if (service.executeCommand(commandToExecute)) {
                    logger.info("&7Executed command on service &a" + serviceName + "&8: &7" + commandToExecute);
                } else {
                    logger.info("&cFailed to execute command on service &a" + serviceName);
                }
            }

            case "logs" -> {
                if (args.length < 2) {
                    logger.info("&cUsage&8: &7service logs &8[&aserviceName&8]");
                    return;
                }
                final String serviceName = args[1];
                final Service service = serviceManager.getService(serviceName);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + serviceName);
                    return;
                }

                List<String> logs = ((ServiceImpl) service).getLogs();
                if (logs.isEmpty()) {
                    logger.info("No logs found for service &a" + serviceName);
                } else {
                    logger.info("Logs for service &a" + serviceName + ":");
                    for (String logLine : logs) {
                        logger.info(logLine);
                    }
                }
            }

            case "screen" -> {
                if (args.length < 2) {
                    logger.info("&cUsage&8: &7service screen &8[&aserviceName&8]");
                    return;
                }
                final String serviceName = args[1];
                final Service service = serviceManager.getService(serviceName);
                if (service == null) {
                    logger.info("&cNo service found with the name &a" + serviceName);
                    return;
                }
                if (service instanceof ServiceImpl impl) {
                    Screen screen = Node.getInstance().getScreenManager().getScreen(impl.getScreen().getName());
                    if (screen == null) {
                        logger.error("Cant switch to screen of service " + serviceName);
                        return;
                    }
                    Node.getInstance().getScreenManager().switchScreen(screen.getName());
                }
            }

            default -> sendHelp();
        }
    }

    private void sendHelp() {
        logger.info("service list &8- &7List all running services");
        logger.info("service start &8[&agroupName&8] [&aamount&8] - &7Start new service(s)");
        logger.info("service stop &8[&aname&8] - &7Stop a running service");
        logger.info("service info &8[&aname&8] - &7Show details of a service");
        logger.info("service execute &8[&aname&8] [&acommand&8...] - &7Execute a command on a service");
        logger.info("service logs &8[&aname&8] - &7Print all logs of a service");
        logger.info("service screen &8[&aname&8] - &7Enter the screen of a service");
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
            return List.of("list", "start", "stop", "info", "execute", "logs", "screen").stream()
                    .filter(input -> input.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        String sub = args[0].toLowerCase();

        if (sub.equalsIgnoreCase("start") || sub.equalsIgnoreCase("stop") || sub.equalsIgnoreCase("info")
                || sub.equalsIgnoreCase("execute") || sub.equalsIgnoreCase("logs") || sub.equalsIgnoreCase("screen")) {
            if (args.length == 2) {
                return serviceManager.getAllServices().stream().map(Service::getName)
                        .toList().stream().filter(input -> input.startsWith(args[1])).toList();
            }
        }

        return List.of();
    }
}