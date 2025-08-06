package net.potatocloud.node.player;

import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.packets.player.CloudPlayerConnectPacket;
import net.potatocloud.node.player.listeners.AddCloudPlayerListener;
import net.potatocloud.node.player.listeners.RemoveCloudPlayerListener;
import net.potatocloud.node.player.listeners.UpdateCloudPlayerListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CloudPlayerManagerImpl implements CloudPlayerManager {

    private final Set<CloudPlayer> onlinePlayers = new HashSet<>();
    private final NetworkServer server;

    public CloudPlayerManagerImpl(NetworkServer server) {
        this.server = server;

        server.registerPacketListener(PacketIds.PLAYER_ADD, new AddCloudPlayerListener(this));
        server.registerPacketListener(PacketIds.PLAYER_REMOVE, new RemoveCloudPlayerListener(this));
        server.registerPacketListener(PacketIds.PLAYER_UPDATE, new UpdateCloudPlayerListener(this));
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
    public Set<CloudPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public void connectPlayerWithService(String playerName, String serviceName) {
        server.broadcastPacket(new CloudPlayerConnectPacket(playerName, serviceName));
    }

    @Override
    public void updatePlayer(CloudPlayer player) {}

}

