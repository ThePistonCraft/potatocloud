package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.GroupCreatePacket;

@RequiredArgsConstructor
public class GroupCreateListener implements PacketListener<GroupCreatePacket> {

    private final ServiceGroupManager groupManager;

    @Override
    public void onPacket(NetworkConnection connection, GroupCreatePacket packet) {
        groupManager.createServiceGroup(
                packet.getName(),
                packet.getPlatformName(),
                packet.getMinOnlineCount(),
                packet.getMaxOnlineCount(),
                packet.getMaxPlayers(),
                packet.getMaxMemory(),
                packet.isFallback(),
                packet.isStatic()
        );
    }
}
