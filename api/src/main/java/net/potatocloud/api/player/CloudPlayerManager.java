package net.potatocloud.api.player;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;

import java.util.List;
import java.util.UUID;

public interface CloudPlayerManager {

    CloudPlayer getCloudPlayer(String username);

    CloudPlayer getCloudPlayer(UUID uniqueId);

    List<CloudPlayer> getOnlinePlayers();

    default List<CloudPlayer> getOnlinePlayersByGroup(ServiceGroup group) {
        return getOnlinePlayers().stream()
                .filter(player -> player.getConnectedService() != null && player.getConnectedService().getServiceGroup().getName().equals(group.getName()))
                .toList();
    }

    void connectPlayerWithService(CloudPlayer player, String serviceName);

    default void connectPlayerWithService(CloudPlayer player, Service service) {
        connectPlayerWithService(player, service.getName());
    }

    void updatePlayer(CloudPlayer player);

}
