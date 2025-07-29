package net.potatocloud.plugin.impl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.packets.player.CloudPlayerAddPacket;
import net.potatocloud.core.networking.packets.player.CloudPlayerRemovePacket;
import net.potatocloud.core.networking.packets.player.CloudPlayerUpdatePacket;
import net.potatocloud.plugin.impl.event.LocalConnectPlayerWithServiceEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class CloudPlayerManagerImpl implements CloudPlayerManager {

    private final Set<CloudPlayer> onlinePlayers = new HashSet<>();
    private final NetworkClient client;

    public void registerPlayer(CloudPlayer player) {
        onlinePlayers.add(player);

        client.send(new CloudPlayerAddPacket(player.getUsername(), player.getUniqueId(), player.getConnectedProxyName()));
    }

    public void unregisterPlayer(CloudPlayer player) {
        onlinePlayers.remove(player);

        client.send(new CloudPlayerRemovePacket(player.getUniqueId()));
    }

    @Override
    public CloudPlayer getCloudPlayer(String username) {
        return onlinePlayers.stream()
                .filter(player -> player.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public CloudPlayer getCloudPlayer(UUID uniqueId) {
        return onlinePlayers.stream()
                .filter(player -> player.getUniqueId().equals(uniqueId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Set<CloudPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public void connectPlayerWithService(String playerName, String serviceName) {
        CloudAPI.getInstance().getEventManager().call(new LocalConnectPlayerWithServiceEvent(playerName, serviceName));
    }

    @Override
    public void updatePlayer(CloudPlayer player) {
        client.send(new CloudPlayerUpdatePacket(player.getUniqueId(), player.getConnectedProxyName(),
                player.getConnectedServiceName(), player.getProperties()));
    }
}

