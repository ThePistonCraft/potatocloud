package net.potatocloud.plugin.impl.service;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.service.RequestServicesPacket;
import net.potatocloud.core.networking.packets.service.ServiceRemovePacket;
import net.potatocloud.core.networking.packets.service.StartServicePacket;
import net.potatocloud.core.networking.packets.service.UpdateServicePacket;
import net.potatocloud.plugin.impl.listener.service.ServiceAddListener;
import net.potatocloud.plugin.impl.listener.service.UpdateServiceListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceManagerImpl implements ServiceManager {

    private final List<Service> services = new CopyOnWriteArrayList<>();
    private final NetworkClient client;

    public ServiceManagerImpl(NetworkClient client) {
        this.client = client;

        client.send(new RequestServicesPacket());

        client.registerPacketListener(PacketTypes.SERVICE_ADD, new ServiceAddListener(this));

        client.registerPacketListener(PacketTypes.SERVICE_REMOVE, (NetworkConnection connection, ServiceRemovePacket packet) -> {
            services.remove(getService(packet.getServiceName()));
        });

        client.registerPacketListener(PacketTypes.UPDATE_SERVICE, new UpdateServiceListener(this));
    }

    public void addService(Service service) {
        services.add(service);
    }

    @Override
    public Service getService(String serviceName) {
        return services.stream()
                .filter(service -> service.getName().equalsIgnoreCase(serviceName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Service> getAllServices() {
        return Collections.unmodifiableList(services);
    }

    @Override
    public void updateService(Service service) {
        client.send(new UpdateServicePacket(
                service.getName(),
                service.getState().name(),
                service.getMaxPlayers()
        ));
    }

    @Override
    public void startService(ServiceGroup serviceGroup) {
        client.send(new StartServicePacket(serviceGroup.getName()));
    }

    @Override
    public void startServices(ServiceGroup serviceGroup, int count) {
        for (int i = 0; i < count; i++) {
            startService(serviceGroup);
        }
    }
}