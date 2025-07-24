package net.potatocloud.api.player;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.service.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public interface CloudPlayerManager {

    CloudPlayer getCloudPlayer(String username);

    CloudPlayer getCloudPlayer(UUID uniqueId);

    Set<CloudPlayer> getOnlinePlayers();

    default Set<CloudPlayer> getOnlinePlayersByGroup(ServiceGroup group) {
        return getOnlinePlayers().stream()
                .filter(player -> player.getConnectedService() != null && player.getConnectedService().getServiceGroup().getName().equals(group.getName()))
                .collect(Collectors.toSet());
    }

    void connectPlayerWithService(CloudPlayer player, String serviceName);

    default void connectPlayerWithService(CloudPlayer player, Service service) {
        connectPlayerWithService(player, service.getName());
    }

    void updatePlayer(CloudPlayer player);

}
