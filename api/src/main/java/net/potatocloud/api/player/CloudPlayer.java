package net.potatocloud.api.player;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.service.Service;

import java.util.UUID;

public interface CloudPlayer {

    String getUsername();

    UUID getUniqueId();

    String getConnectedProxyName();

    String getConnectedServiceName();

    default Service getConnectedProxy() {
        return CloudAPI.getInstance().getServiceManager().getService(getConnectedServiceName());
    }

    default Service getConnectedService() {
        return CloudAPI.getInstance().getServiceManager().getService(getConnectedServiceName());
    }

    default void connectWithService(Service service) {
        CloudAPI.getInstance().getPlayerManager().connectPlayerWithService(this, service);
    }

    default void connectWithService(String serviceName) {
        connectWithService(CloudAPI.getInstance().getServiceManager().getService(serviceName));
    }

    default void update() {
        CloudAPI.getInstance().getPlayerManager().updatePlayer(this);
    }
}
