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
                .filter(player -> player.getConnectedService() != null && player.getConnectedService().getServiceGroup().equals(group))
                .toList();
    }

    void connectPlayerWithService(CloudPlayer player, Service service);

    void updatePlayer(CloudPlayer player);

}
