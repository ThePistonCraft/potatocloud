package net.potatocloud.node.listeners.group;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.UpdateGroupPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.group.ServiceGroupManagerImpl;
import net.potatocloud.node.group.ServiceGroupStorage;

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

        group.getServiceTemplates().clear();
        packet.getServiceTemplates().forEach(group::addServiceTemplate);

        group.getCustomJvmFlags().clear();
        for (String flag : packet.getCustomJvmFlags()) {
            group.addCustomJvmFlag(flag);
        }

        group.getProperties().clear();
        for (PropertyData data : packet.getProperties()) {
            group.setProperty(Property.fromData(data));
        }

        // update group file
        if (groupManager instanceof ServiceGroupManagerImpl impl) {
            ServiceGroupStorage.saveToFile(group, impl.getGroupsPath());
        }

        Node.getInstance().getServer().getConnectedSessions().stream()
                .filter(networkConnection -> !networkConnection.equals(connection))
                .forEach(networkConnection -> networkConnection.send(packet));
    }
}
