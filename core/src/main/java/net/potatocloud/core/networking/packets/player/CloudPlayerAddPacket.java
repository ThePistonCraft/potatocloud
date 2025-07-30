package net.potatocloud.core.networking.packets.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.netty.PacketBuffer;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlayerAddPacket implements Packet {

    private String username;
    private UUID uniqueId;
    private String connectedProxyName;

    @Override
    public int getId() {
        return PacketIds.PLAYER_ADD;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(username);
        buf.writeString(uniqueId.toString());
        buf.writeString(connectedProxyName);
    }

    @Override
    public void read(PacketBuffer buf) {
        username = buf.readString();
        uniqueId = UUID.fromString(buf.readString());
        connectedProxyName = buf.readString();
    }
}
