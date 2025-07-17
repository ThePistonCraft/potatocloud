package net.potatocloud.node.listeners.group;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.DeleteGroupPacket;

@RequiredArgsConstructor
public class DeleteGroupListener implements PacketListener<DeleteGroupPacket> {

    private final ServiceGroupManager groupManager;

    @Override
    public void onPacket(NetworkConnection connection, DeleteGroupPacket packet) {
        final ServiceGroup groupToDelete = groupManager.getServiceGroup(packet.getGroupName());
        if (groupToDelete == null) {
            return;
        }
        groupManager.deleteServiceGroup(groupToDelete);
    }
}
