package net.potatocloud.plugin.impl.listener.group;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.UpdateGroupPacket;

@RequiredArgsConstructor
public class UpdateGroupListener implements PacketListener<UpdateGroupPacket> {

    private final ServiceGroupManager serviceGroupManager;

    @Override
    public void onPacket(NetworkConnection connection, UpdateGroupPacket packet) {
        final ServiceGroup group = serviceGroupManager.getServiceGroup(packet.getGroupName());
        group.setMinOnlineCount(packet.getMinOnlineCount());
        group.setMaxOnlineCount(packet.getMaxOnlineCount());
        group.setMaxPlayers(packet.getMaxPlayers());
        group.setMaxMemory(packet.getMaxMemory());
        group.setFallback(packet.isFallback());
        packet.getServiceTemplates().forEach(group::addServiceTemplate);
    }
}
