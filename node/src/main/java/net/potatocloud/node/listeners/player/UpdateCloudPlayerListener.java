package net.potatocloud.node.listeners.player;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.player.UpdateCloudPlayerPacket;
import net.potatocloud.node.Node;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UpdateCloudPlayerListener implements PacketListener<UpdateCloudPlayerPacket> {

    private final CloudPlayerManager playerManager;

    @Override
    public void onPacket(NetworkConnection connection, UpdateCloudPlayerPacket packet) {
        final CloudPlayerImpl player = (CloudPlayerImpl) playerManager.getCloudPlayer(packet.getPlayerUniqueId());
        player.setConnectedProxyName(packet.getConnectedProxyName());
        player.setConnectedServiceName(packet.getConnectedServiceName());

        player.getProperties().clear();
        for (PropertyData data : packet.getProperties()) {
            player.setProperty(Property.fromData(data));
        }

        // send back the same packet for the paper clients
        Node.getInstance().getServer().broadcastPacket(new UpdateCloudPlayerPacket(player.getUniqueId(), player.getConnectedProxyName(), player.getConnectedServiceName(), player.getProperties().stream().map(Property::getData).collect(Collectors.toSet())));
    }
}
