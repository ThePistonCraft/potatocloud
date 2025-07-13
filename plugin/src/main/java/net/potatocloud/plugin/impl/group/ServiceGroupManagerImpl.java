package net.potatocloud.plugin.impl.group;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.group.AddGroupPacket;
import net.potatocloud.core.networking.packets.group.RequestGroupsPacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> serviceGroups = new ArrayList<>();

    public ServiceGroupManagerImpl() {
        final NetworkClient client = PluginCloudAPI.getInstance().getClient();

        client.send(new RequestGroupsPacket());

        client.registerPacketListener(PacketTypes.GROUP_ADD, (NetworkConnection connection, AddGroupPacket packet) -> {
            serviceGroups.add(new ServiceGroupImpl(
                    packet.getName(),
                    packet.getMinOnlineCount(),
                    packet.getMaxOnlineCount(),
                    packet.getMaxPlayers(),
                    packet.getMaxMemory(),
                    packet.isFallback(),
                    packet.isStatic(),
                    packet.getPlatformName(),
                    packet.getServiceTemplates()
            ));
        });
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
    public ServiceGroup createServiceGroup(String name, int minOnlineCount, int maxOnlineCount, int maxPlayers, int maxMemory, boolean fallback, boolean isStatic, String platformName) {
        //todo
        return null;
    }


    @Override
    public boolean deleteServiceGroup(ServiceGroup group) {
        //todo
        return false;
    }

    @Override
    public void updateServiceGroup(ServiceGroup group) {
        //todo
    }

    @Override
    public boolean existsServiceGroup(String groupName) {
        if (groupName == null) {
            return false;
        }
        return serviceGroups.stream().anyMatch(serviceGroup -> serviceGroup != null && serviceGroup.getName().equalsIgnoreCase(groupName));
    }
}
