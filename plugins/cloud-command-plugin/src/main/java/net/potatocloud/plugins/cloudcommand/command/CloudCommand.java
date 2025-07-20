package net.potatocloud.plugins.cloudcommand.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.potatocloud.plugins.cloudcommand.MessagesConfig;

import java.util.List;

@RequiredArgsConstructor
public class CloudCommand implements SimpleCommand {

    private final MessagesConfig messages;

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            return;
        }

        //todo add permission back
        /*
        if (!player.hasPermission("potatocloud.cloudcommand")) {
            player.sendMessage(messages.get("no-permission"));
            return;
        }
         */

        final String[] args = invocation.arguments();

        if (args.length == 0) {
            sendHelp(player);
            return;
        }

        final String commandName = args[0].toLowerCase();

        switch (commandName) {
            case "group" -> handleGroupCommand(player, args);
            case "service" -> {

            }
            case "player" -> {

            }
            default -> sendHelp(player);
        }
    }

    private void handleGroupCommand(Player player, String[] args) {
        final GroupSubCommand groupSubCommand = new GroupSubCommand(player, messages);

        if (args.length < 2) {
            sendHelpGroup(player);
            return;
        }

        final String sub = args[1].toLowerCase();

        switch (sub) {
            case "list" -> groupSubCommand.listGroups();
            case "info" -> groupSubCommand.infoGroup(args);
            case "shutdown" -> groupSubCommand.shutdownGroup(args);
            case "edit" -> groupSubCommand.editGroup(args);
            case "property" -> groupSubCommand.propertyGroup(args);
            default -> sendHelpGroup(player);
        }
    }

    private void sendHelpGroup(Player player) {
        player.sendMessage(messages.get("group.help.create"));
        player.sendMessage(messages.get("group.help.delete"));
        player.sendMessage(messages.get("group.help.list"));
        player.sendMessage(messages.get("group.help.info"));
        player.sendMessage(messages.get("group.help.shutdown"));
        player.sendMessage(messages.get("group.help.edit"));
        player.sendMessage(messages.get("group.help.edit.addTemplate"));
        player.sendMessage(messages.get("group.help.edit.removeTemplate"));
        player.sendMessage(messages.get("group.help.edit.addJvmFlag"));
        player.sendMessage(messages.get("group.help.property"));
    }

    private void sendHelp(Player player) {

    }

    @Override
    public List<String> suggest(Invocation invocation) {
        final String[] args = invocation.arguments();

        if (args.length == 0) {
            return List.of("group", "service", "player");
        }

        if (args.length == 1) {
            return List.of("group", "service", "player").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        final String commandName = args[0].toLowerCase();

        return switch (commandName) {
            case "group" -> new GroupSubCommand(null, null).suggest(args);
            case "service" -> List.of();
            case "player" -> List.of();
            default -> List.of();
        };
    }
}
