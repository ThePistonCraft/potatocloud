package net.potatocloud.plugin.impl.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.packets.player.AddCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.RemoveCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.UpdateCloudPlayerPacket;
import net.potatocloud.plugin.impl.event.LocalConnectPlayerWithServiceEvent;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class CloudPlayerManagerImpl implements CloudPlayerManager {

    private final Set<CloudPlayer> onlinePlayers = new HashSet<>();
    private final NetworkClient client;

    public void registerPlayer(CloudPlayer player) {
        onlinePlayers.add(player);

        client.send(new AddCloudPlayerPacket(player.getUsername(), player.getUniqueId(), player.getConnectedProxyName()));
    }

    public void unregisterPlayer(CloudPlayer player) {
        onlinePlayers.remove(player);

        client.send(new RemoveCloudPlayerPacket(player.getUniqueId()));
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
        CloudAPI.getInstance().getEventManager().callLocal(new LocalConnectPlayerWithServiceEvent(playerName, serviceName));
    }

    @Override
    public void updatePlayer(CloudPlayer player) {
        client.send(new UpdateCloudPlayerPacket(player.getUniqueId(), player.getConnectedProxyName(),
                player.getConnectedServiceName(), player.getProperties().stream().map(Property::getData).collect(Collectors.toSet())));
    }
}

