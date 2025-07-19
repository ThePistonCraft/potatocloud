package net.potatocloud.plugins.cloudcommand.command;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.potatocloud.plugins.cloudcommand.MessagesConfig;

@RequiredArgsConstructor
public class CloudCommand implements SimpleCommand {

    private final MessagesConfig messages;

    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            return;
        }

        if (!player.hasPermission("potatocloud.cloudcommand")) {
            player.sendMessage(messages.get("no-permission"));
            return;
        }

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
            sendHelp(player);
            return;
        }

        final String sub = args[1].toLowerCase();

        switch (sub) {
            case "list" -> groupSubCommand.listGroups();
            case "delete" -> groupSubCommand.deleteGroup(args);
            case "info" -> groupSubCommand.infoGroup(args);
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
}
