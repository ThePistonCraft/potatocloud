package net.potatocloud.plugin.impl.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.service.RequestServicesPacket;
import net.potatocloud.core.networking.packets.service.ServiceAddPacket;
import net.potatocloud.core.networking.packets.service.ServiceRemovePacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceManagerImpl implements ServiceManager {

    private final List<Service> onlineServices = new CopyOnWriteArrayList<>();
    private final NetworkClient client;

    public ServiceManagerImpl() {
        this.client = PluginCloudAPI.getInstance().getClient();

        client.registerPacketListener(PacketTypes.SERVICE_ADD, (NetworkConnection connection, ServiceAddPacket packet) -> {
            final Service service = new ServiceImpl(
                    packet.getName(),
                    packet.getServiceId(),
                    packet.getPort(),
                    packet.getStartTimestamp(),
                    CloudAPI.getInstance().getServiceGroupManager().getServiceGroup(packet.getGroupName()),
                    ServiceState.valueOf(packet.getState()),
                    packet.getOnlinePlayers(),
                    packet.getUsedMemory());

            if (!onlineServices.contains(service)) {
                onlineServices.add(service);
            }
        });

        client.send(new RequestServicesPacket());

        client.registerPacketListener(PacketTypes.SERVICE_REMOVE, (NetworkConnection connection, ServiceRemovePacket packet) -> {
            onlineServices.remove(getService(packet.getServiceName()));
        });
    }

    @Override
    public Service getService(String serviceName) {
        return onlineServices.stream()
                .filter(service -> service.getName().equalsIgnoreCase(serviceName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Service> getAllOnlineServices() {
        return onlineServices;
    }

    @Override
    public void startService(ServiceGroup serviceGroup) {
        // todo
    }

    @Override
    public void startServices(ServiceGroup serviceGroup, int count) {
        for (int i = 0; i < count; i++) {
            startService(serviceGroup);
        }
    }
}