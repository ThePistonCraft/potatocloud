package net.potatocloud.plugins.cloudcommand.command;

import com.velocitypowered.api.proxy.Player;
import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.plugins.cloudcommand.MessagesConfig;

import java.util.List;

@RequiredArgsConstructor
public class GroupSubCommand {

    private final Player player;
    private final MessagesConfig messages;

    public void listGroups() {
        final List<ServiceGroup> groups = CloudAPI.getInstance().getServiceGroupManager().getAllServiceGroups();

        for (ServiceGroup group : groups) {
            player.sendMessage(messages.get("group.list.entry")
                    .replaceText(text -> text.match("%name%").replacement(group.getName())));
        }
    }

    public void deleteGroup(String[] args) {
        if (args.length < 3) {
            player.sendMessage(messages.get("group.delete.usage"));
            return;
        }

        final ServiceGroupManager groupManager = CloudAPI.getInstance().getServiceGroupManager();

        final String name = args[2];
        if (!groupManager.existsServiceGroup(name)) {
            player.sendMessage(messages.get("no-group")
                    .replaceText(text -> text.match("%name%").replacement(name)));
            return;
        }

        final ServiceGroup group = groupManager.getServiceGroup(name);
        groupManager.deleteServiceGroup(group);
        player.sendMessage(messages.get("group.delete.success")
                .replaceText(text -> text.match("%name%").replacement(name)));
    }

    public void infoGroup(String[] args) {
        if (args.length < 3) {
            player.sendMessage(messages.get("group.info.usage"));
            return;
        }

        final ServiceGroupManager groupManager = CloudAPI.getInstance().getServiceGroupManager();

        final String name = args[2];
        if (!groupManager.existsServiceGroup(name)) {
            player.sendMessage(messages.get("no-group")
                    .replaceText(text -> text.match("%name%").replacement(name)));
            return;
        }

        final ServiceGroup group = groupManager.getServiceGroup(name);
        player.sendMessage(messages.get("group.info.name").replaceText(text -> text.match("%name%").replacement(name)));
        player.sendMessage(messages.get("group.info.platform")
                .replaceText(text -> text.match("%platform%").replacement(group.getPlatform().getFullName())));
        player.sendMessage(messages.get("group.info.templates")
                .replaceText(text -> text.match("%templates%").replacement(String.join(", ", group.getServiceTemplates()))));
        player.sendMessage(messages.get("group.info.min-online")
                .replaceText(text -> text.match("%minOnline%").replacement(String.valueOf(group.getMinOnlineCount()))));
        player.sendMessage(messages.get("group.info.max-online")
                .replaceText(text -> text.match("%maxOnline%").replacement(String.valueOf(group.getMaxOnlineCount()))));
        player.sendMessage(messages.get("group.info.online-players")
                .replaceText(text -> text.match("%onlinePlayers%")
                        .replacement(String.valueOf(CloudAPI.getInstance().getPlayerManager().getOnlinePlayersByGroup(group).size()))));
        player.sendMessage(messages.get("group.info.max-players")
                .replaceText(text -> text.match("%maxPlayers%").replacement(String.valueOf(group.getMaxPlayers()))));
        player.sendMessage(messages.get("group.info.fallback")
                .replaceText(text -> text.match("%fallback%").replacement((group.isFallback() ? "<green>Yes" : "<red>No"))));
        player.sendMessage(messages.get("group.info.static")
                .replaceText(text -> text.match("%static%").replacement((group.isFallback() ? "<green>Yes" : "<red>No"))));

    }
}
