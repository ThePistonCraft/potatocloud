package net.potatocloud.node.group.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.GroupDeletePacket;

@RequiredArgsConstructor
public class DeleteGroupListener implements PacketListener<GroupDeletePacket> {

    private final ServiceGroupManager groupManager;

    @Override
    public void onPacket(NetworkConnection connection, GroupDeletePacket packet) {
        final ServiceGroup group = groupManager.getServiceGroup(packet.getGroupName());
        if (group == null) {
            return;
        }
        groupManager.deleteServiceGroup(group);
    }
}
