package net.potatocloud.plugin.impl;

import lombok.Getter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.event.ClientEventManager;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.PacketManager;
import net.potatocloud.core.networking.netty.NettyNetworkClient;
import net.potatocloud.plugin.impl.group.ServiceGroupManagerImpl;
import net.potatocloud.plugin.impl.service.ServiceManagerImpl;

@Getter
public class PluginCloudAPI extends CloudAPI {

    private final PacketManager packetManager;
    private final NetworkClient client;
    private final ServiceGroupManager serviceGroupManager;
    private final ServiceManager serviceManager;

    public PluginCloudAPI() {
        packetManager = new PacketManager();
        client = new NettyNetworkClient(packetManager);
        client.connect("0.0.0.0", 9000);
        serviceGroupManager = new ServiceGroupManagerImpl(client);
        serviceManager = new ServiceManagerImpl(client);
    }

    public static PluginCloudAPI getInstance() {
        return (PluginCloudAPI) CloudAPI.getInstance();
    }

    public void shutdown() {
        client.disconnect();
    }

    @Override
    public ServiceGroupManager getServiceGroupManager() {
        return serviceGroupManager;
    }

    @Override
    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    @Override
    public EventManager getEventManager() {
        return new ClientEventManager(client);
    }
}
