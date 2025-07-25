package net.potatocloud.plugin.impl.group;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.group.CreateGroupPacket;
import net.potatocloud.core.networking.packets.group.DeleteGroupPacket;
import net.potatocloud.core.networking.packets.group.RequestGroupsPacket;
import net.potatocloud.core.networking.packets.group.UpdateGroupPacket;
import net.potatocloud.plugin.impl.listener.group.AddGroupListener;
import net.potatocloud.plugin.impl.listener.group.UpdateGroupListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> groups = new ArrayList<>();
    private final NetworkClient client;

    public ServiceGroupManagerImpl(NetworkClient client) {
        this.client = client;

        client.send(new RequestGroupsPacket());

        client.registerPacketListener(PacketTypes.GROUP_ADD, new AddGroupListener(this));
        client.registerPacketListener(PacketTypes.UPDATE_GROUP, new UpdateGroupListener(this));
    }

    public void addServiceGroup(ServiceGroup group) {
        groups.add(group);
    }

    @Override
    public ServiceGroup getServiceGroup(String name) {
        return groups.stream()
                .filter(cloudServiceGroup -> cloudServiceGroup.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        return Collections.unmodifiableList(groups);
    }

    @Override
    public ServiceGroup createServiceGroup(String name, String platformName, int minOnlineCount, int maxOnlineCount, int maxPlayers, int maxMemory, boolean fallback, boolean isStatic) {
        client.send(new CreateGroupPacket(
                name,
                platformName,
                minOnlineCount,
                maxOnlineCount,
                maxPlayers,
                maxMemory,
                fallback,
                isStatic
        ));
        return null;
    }


    @Override
    public void deleteServiceGroup(String name) {
        client.send(new DeleteGroupPacket(name));
    }

    @Override
    public void updateServiceGroup(ServiceGroup group) {
        client.send(new UpdateGroupPacket(
                group.getName(),
                group.getMinOnlineCount(),
                group.getMaxOnlineCount(),
                group.getMaxPlayers(),
                group.getMaxMemory(),
                group.isFallback(),
                group.getServiceTemplates(),
                group.getProperties().stream().map(Property::getData).collect(Collectors.toSet()),
                group.getCustomJvmFlags()
        ));
    }

    @Override
    public boolean existsServiceGroup(String groupName) {
        if (groupName == null) {
            return false;
        }
        return groups.stream().anyMatch(serviceGroup -> serviceGroup != null && serviceGroup.getName().equalsIgnoreCase(groupName));
    }
}
