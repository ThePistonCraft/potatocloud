package net.potatocloud.node.service;

import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.service.ServiceAddPacket;
import net.potatocloud.core.networking.packets.service.UpdateServicePacket;
import net.potatocloud.node.config.NodeConfig;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.listeners.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServiceManagerImpl implements ServiceManager {

    private final List<Service> services = new CopyOnWriteArrayList<>();

    private final NodeConfig config;
    private final Logger logger;
    private final NetworkServer server;
    private final EventManager eventManager;
    private final ServiceGroupManager groupManager;

    public ServiceManagerImpl(NodeConfig config, Logger logger, NetworkServer server, EventManager eventManager, ServiceGroupManager groupManager) {
        this.config = config;
        this.logger = logger;
        this.server = server;
        this.eventManager = eventManager;
        this.groupManager = groupManager;

        server.registerPacketListener(PacketTypes.REQUEST_SERVICES, new RequestServicesListener(this));
        server.registerPacketListener(PacketTypes.SERVICE_STARTED, new ServiceStartedListener(this, logger, eventManager));
        server.registerPacketListener(PacketTypes.UPDATE_SERVICE, new UpdateServiceListener(this));
        server.registerPacketListener(PacketTypes.START_SERVICE, new StartServiceListener(this, groupManager));
        server.registerPacketListener(PacketTypes.SHUTDOWN_SERVICE, new ShutdownServiceListener(this));
        server.registerPacketListener(PacketTypes.SERVICE_EXECUTE_COMMAND, new ServiceExecuteCommandListener(this));
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
        return services;
    }

    @Override
    public void updateService(Service service) {
        server.broadcastPacket(new UpdateServicePacket(
                service.getName(),
                service.getState().name(),
                service.getMaxPlayers()
        ));
    }

    @Override
    public void startService(ServiceGroup serviceGroup) {
        final int serviceId = getFreeServiceId(serviceGroup);
        final int port = getServicePort(serviceGroup);
        final ServiceImpl service = new ServiceImpl(serviceId, port, serviceGroup, config, logger);

        services.add(service);

        // broadcast add service packet to all connected clients
        server.broadcastPacket(new ServiceAddPacket(service.getName(), service.getServiceId(), service.getPort(), service.getStartTimestamp(), service.getServiceGroup().getName(), service.getState().name(), service.getOnlinePlayers(), service.getUsedMemory()));

        service.start();
    }

    @Override
    public void startServices(ServiceGroup serviceGroup, int count) {
        for (int i = 0; i < count; i++) {
            startService(serviceGroup);
        }
    }

    public void removeService(Service service) {
        services.remove(service);
    }

    private int getFreeServiceId(ServiceGroup serviceGroup) {
        final List<Integer> usedIds = new ArrayList<>();

        for (Service service : services) {
            if (service.getServiceGroup().equals(serviceGroup)) {
                usedIds.add(service.getServiceId());
            }
        }

        int id = 1;
        while (usedIds.contains(id)) {
            id++;
        }

        return id;
    }

    private int getServicePort(ServiceGroup serviceGroup) {
        final List<Integer> usedPorts = new ArrayList<>();
        for (Service service : services) {
            usedPorts.add(service.getPort());
        }

        int port = serviceGroup.getPlatform().isProxy() ? config.getProxyStartPort() : config.getServiceStartPort();

        while (usedPorts.contains(port)) {
            port++;
        }

        return port;
    }
}
