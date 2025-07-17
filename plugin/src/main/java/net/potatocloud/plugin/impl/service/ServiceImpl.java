package net.potatocloud.plugin.impl.service;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;

@Getter
@Setter
public class ServiceImpl implements Service {

    private final String name;
    private final int serviceId;
    private final int port;
    private final long startTimestamp;
    private final ServiceGroup serviceGroup;

    private ServiceState state;
    private int onlinePlayers;
    private int usedMemory;
    private int maxPlayers;

    public ServiceImpl(String name, int serviceId, int port, long startTimestamp, ServiceGroup serviceGroup, ServiceState serviceState, int onlinePlayers, int usedMemory) {
        this.name = name;
        this.serviceId = serviceId;
        this.port = port;
        this.startTimestamp = startTimestamp;
        this.serviceGroup = serviceGroup;
        this.state = serviceState;
        this.onlinePlayers = onlinePlayers;
        this.usedMemory = usedMemory;

        maxPlayers = serviceGroup.getMaxPlayers();
    }

    public boolean isOnline() {
        return state == ServiceState.RUNNING;
    }

    @Override
    public void shutdown() {
        // todo: send packet to node
    }

    @Override
    public boolean executeCommand(String command) {
        // todo: also, send packet to node
        return false;
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }
}
