package net.potatocloud.core.networking.packets.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.netty.PacketBuffer;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlayerUpdatePacket implements Packet {

    private UUID playerUniqueId;
    private String connectedProxyName;
    private String connectedServiceName;
    private Set<Property> properties;

    @Override
    public int getId() {
        return PacketIds.PLAYER_UPDATE;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(playerUniqueId.toString());
        buf.writeString(connectedProxyName);
        buf.writeString(connectedServiceName);
        buf.writePropertySet(properties);
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUniqueId = UUID.fromString(buf.readString());
        connectedProxyName = buf.readString();
        connectedServiceName = buf.readString();
        properties = buf.readPropertySet();
    }
}
