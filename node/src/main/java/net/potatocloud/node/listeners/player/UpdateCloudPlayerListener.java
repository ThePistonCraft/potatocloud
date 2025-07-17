package net.potatocloud.node.listeners.player;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.player.UpdateCloudPlayerPacket;

@RequiredArgsConstructor
public class UpdateCloudPlayerListener implements PacketListener<UpdateCloudPlayerPacket> {

    private final CloudPlayerManager playerManager;

    @Override
    public void onPacket(NetworkConnection connection, UpdateCloudPlayerPacket packet) {
        final CloudPlayerImpl player = (CloudPlayerImpl) playerManager.getCloudPlayer(packet.getPlayerUniqueId());
        player.setConnectedProxyName(packet.getConnectedProxyName());
        player.setConnectedServiceName(packet.getConnectedServiceName());
    }
}
