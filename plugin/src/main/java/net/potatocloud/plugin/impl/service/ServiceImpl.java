package net.potatocloud.plugin.impl.service;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.packets.service.ServiceCopyPacket;
import net.potatocloud.core.networking.packets.service.ServiceExecuteCommandPacket;
import net.potatocloud.core.networking.packets.service.ShutdownServicePacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ServiceImpl implements Service {

    private final String name;
    private final int serviceId;
    private final int port;
    private final long startTimestamp;
    private final ServiceGroup group;
    private final NetworkClient client;
    private final Set<Property> properties;
    private ServiceStatus status;
    private int usedMemory;
    private int maxPlayers;

    public ServiceImpl(String name, int serviceId, int port, long startTimestamp, ServiceGroup group, ServiceStatus status, int usedMemory) {
        this.name = name;
        this.serviceId = serviceId;
        this.port = port;
        this.startTimestamp = startTimestamp;
        this.group = group;
        this.status = status;
        this.usedMemory = usedMemory;

        maxPlayers = group.getMaxPlayers();
        client = PluginCloudAPI.getInstance().getClient();
        properties = new HashSet<>(group.getProperties());
    }

    public boolean isOnline() {
        return status.equals(ServiceStatus.RUNNING);
    }

    @Override
    public List<CloudPlayer> getOnlinePlayers() {
        return CloudAPI.getInstance().getPlayerManager().getOnlinePlayers().stream().filter(player -> player.getConnectedServiceName().equals(name)).toList();
    }

    @Override
    public int getOnlinePlayersCount() {
        return getOnlinePlayers().size();
    }

    @Override
    public ServiceGroup getServiceGroup() {
        return group;
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
    public void copy(String template, String filter) {
        client.send(new ServiceCopyPacket(getName(), template, filter));
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }

    @Override
    public String getPropertyHolderName() {
        return getName();
    }
}
