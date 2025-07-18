package net.potatocloud.plugin.impl.service;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.packets.service.ServiceExecuteCommandPacket;
import net.potatocloud.core.networking.packets.service.ShutdownServicePacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;

@Getter
@Setter
public class ServiceImpl implements Service {

    private final String name;
    private final int serviceId;
    private final int port;
    private final long startTimestamp;
    private final ServiceGroup serviceGroup;
    private final NetworkClient client;
    private ServiceStatus status;
    private int onlinePlayers;
    private int usedMemory;
    private int maxPlayers;

    public ServiceImpl(String name, int serviceId, int port, long startTimestamp, ServiceGroup serviceGroup, ServiceStatus status, int onlinePlayers, int usedMemory) {
        this.name = name;
        this.serviceId = serviceId;
        this.port = port;
        this.startTimestamp = startTimestamp;
        this.serviceGroup = serviceGroup;
        this.status = status;
        this.onlinePlayers = onlinePlayers;
        this.usedMemory = usedMemory;

        maxPlayers = serviceGroup.getMaxPlayers();
        client = PluginCloudAPI.getInstance().getClient();
    }

    public boolean isOnline() {
        return status.equals(ServiceStatus.RUNNING);
    }

    @Override
    public void shutdown() {
        client.send(new ShutdownServicePacket(name));
    }

    @Override
    public boolean executeCommand(String command) {
        client.send(new ServiceExecuteCommandPacket(name, command));
        return false;
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }
}
