package net.potatocloud.node.listeners.group;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.AddGroupPacket;
import net.potatocloud.core.networking.packets.group.RequestGroupsPacket;

@RequiredArgsConstructor
public class RequestGroupsListener implements PacketListener<RequestGroupsPacket> {

    private final ServiceGroupManager groupManager;

    @Override
    public void onPacket(NetworkConnection connection, RequestGroupsPacket packet) {
        for (ServiceGroup group : groupManager.getAllServiceGroups()) {
            connection.send(new AddGroupPacket(
                    group.getName(),
                    group.getMinOnlineCount(),
                    group.getMaxOnlineCount(),
                    group.getMaxPlayers(),
                    group.getMaxMemory(),
                    group.isFallback(),
                    group.isStatic(),
                    group.getPlatformName(),
                    group.getServiceTemplates()
            ));
        }
    }
}
