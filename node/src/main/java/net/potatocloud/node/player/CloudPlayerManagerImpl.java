package net.potatocloud.node.player;

import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.player.ConnectCloudPlayerWithServicePacket;
import net.potatocloud.node.listeners.player.AddCloudPlayerListener;
import net.potatocloud.node.listeners.player.RemoveCloudPlayerListener;
import net.potatocloud.node.listeners.player.UpdateCloudPlayerListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CloudPlayerManagerImpl implements CloudPlayerManager {

    private final List<CloudPlayer> onlinePlayers = new ArrayList<>();
    private final NetworkServer server;

    public CloudPlayerManagerImpl(NetworkServer server) {
        this.server = server;

        server.registerPacketListener(PacketTypes.PLAYER_ADD, new AddCloudPlayerListener(this));
        server.registerPacketListener(PacketTypes.PLAYER_REMOVE, new RemoveCloudPlayerListener(this));
        server.registerPacketListener(PacketTypes.UPDATE_PLAYER, new UpdateCloudPlayerListener(this));
    }

    public void registerPlayer(CloudPlayer player) {
        onlinePlayers.add(player);
    }

    public void unregisterPlayer(CloudPlayer player) {
        onlinePlayers.remove(player);
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
        server.broadcastPacket(new ConnectCloudPlayerWithServicePacket(player.getUniqueId(), service.getName()));
    }

    @Override
    public void updatePlayer(CloudPlayer player) {
    }

}

