package net.potatocloud.api.player;

import net.potatocloud.api.service.Service;

import java.util.List;
import java.util.UUID;

public interface CloudPlayerManager {

    CloudPlayer getCloudPlayer(String username);

    CloudPlayer getCloudPlayer(UUID uniqueId);

    List<CloudPlayer> getOnlinePlayers();

    void connectPlayerWithService(CloudPlayer player, Service service);

    void updatePlayer(CloudPlayer player);

}
