package net.potatocloud.node.listeners.player;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.player.RemoveCloudPlayerPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.player.CloudPlayerManagerImpl;

@RequiredArgsConstructor
public class RemoveCloudPlayerListener implements PacketListener<RemoveCloudPlayerPacket> {

    private final CloudPlayerManagerImpl playerManager;

    @Override
    public void onPacket(NetworkConnection connection, RemoveCloudPlayerPacket packet) {
        final CloudPlayer playerToRemove = playerManager.getCloudPlayer(packet.getPlayerUniqueId());
        playerManager.unregisterPlayer(playerToRemove);

        Node.getInstance().getServer().getConnectedSessions().stream()
                .filter(networkConnection -> !networkConnection.equals(connection))
                .forEach(networkConnection -> networkConnection.send(packet));
    }
}
