package net.potatocloud.plugin.impl.listener.group;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.UpdateGroupPacket;

@RequiredArgsConstructor
public class UpdateGroupListener implements PacketListener<UpdateGroupPacket> {

    private final ServiceGroupManager groupManager;

    @Override
    public void onPacket(NetworkConnection connection, UpdateGroupPacket packet) {
        final ServiceGroup group = groupManager.getServiceGroup(packet.getGroupName());
        group.setMinOnlineCount(packet.getMinOnlineCount());
        group.setMaxOnlineCount(packet.getMaxOnlineCount());
        group.setMaxPlayers(packet.getMaxPlayers());
        group.setMaxMemory(packet.getMaxMemory());
        group.setFallback(packet.isFallback());
        packet.getServiceTemplates().forEach(group::addServiceTemplate);
        for (PropertyData data : packet.getProperties()) {
            group.setProperty(Property.fromData(data));
        }
    }
}
