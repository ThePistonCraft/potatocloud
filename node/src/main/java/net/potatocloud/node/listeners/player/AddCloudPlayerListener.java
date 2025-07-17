package net.potatocloud.node.listeners.player;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.player.AddCloudPlayerPacket;
import net.potatocloud.node.player.CloudPlayerManagerImpl;

@RequiredArgsConstructor
public class AddCloudPlayerListener implements PacketListener<AddCloudPlayerPacket> {

    private final CloudPlayerManagerImpl playerManager;

    @Override
    public void onPacket(NetworkConnection connection, AddCloudPlayerPacket packet) {
        playerManager.registerPlayer(new CloudPlayerImpl(packet.getUsername(), packet.getUniqueId(), packet.getConnectedProxyName()));
    }
}
