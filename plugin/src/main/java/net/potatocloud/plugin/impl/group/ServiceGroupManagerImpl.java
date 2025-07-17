package net.potatocloud.plugin.impl.group;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
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

public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> serviceGroups = new ArrayList<>();
    private final NetworkClient client;

    public ServiceGroupManagerImpl(NetworkClient client) {
        this.client = client;

        client.send(new RequestGroupsPacket());

        client.registerPacketListener(PacketTypes.GROUP_ADD, new AddGroupListener(this));
        client.registerPacketListener(PacketTypes.UPDATE_GROUP, new UpdateGroupListener(this));
    }

    public void addServiceGroup(ServiceGroup group) {
        serviceGroups.add(group);
    }

    @Override
    public ServiceGroup getServiceGroup(String name) {
        return serviceGroups.stream()
                .filter(cloudServiceGroup -> cloudServiceGroup.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        return Collections.unmodifiableList(serviceGroups);
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
    public boolean deleteServiceGroup(ServiceGroup group) {
        client.send(new DeleteGroupPacket(group.getName()));
        return false;
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
                group.getServiceTemplates()
        ));
    }

    @Override
    public boolean existsServiceGroup(String groupName) {
        if (groupName == null) {
            return false;
        }
        return serviceGroups.stream().anyMatch(serviceGroup -> serviceGroup != null && serviceGroup.getName().equalsIgnoreCase(groupName));
    }
}
