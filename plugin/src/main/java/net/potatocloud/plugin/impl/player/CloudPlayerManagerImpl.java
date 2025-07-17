package net.potatocloud.plugin.impl.player;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.packets.player.AddCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.RemoveCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.UpdateCloudPlayerPacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;
import net.potatocloud.plugin.impl.event.LocalConnectPlayerWithServiceEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CloudPlayerManagerImpl implements CloudPlayerManager {

    private final List<CloudPlayer> onlinePlayers = new ArrayList<>();
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
    public List<CloudPlayer> getOnlinePlayers() {
        return Collections.unmodifiableList(onlinePlayers);
    }

    @Override
    public void connectPlayerWithService(CloudPlayer player, Service service) {
        PluginCloudAPI.getInstance().getEventManager().callLocal(new LocalConnectPlayerWithServiceEvent(player.getUniqueId(), service.getName()));
    }

    @Override
    public void updatePlayer(CloudPlayer player) {
        client.send(new UpdateCloudPlayerPacket(player.getUniqueId(), player.getConnectedProxyName(), player.getConnectedServiceName()));
    }
}

