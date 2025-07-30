package net.potatocloud.core.networking.packets.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.netty.PacketBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudPlayerConnectPacket implements Packet {

    private String playerUsername;
    private String serviceName;

    @Override
    public int getId() {
        return PacketIds.PLAYER_CONNECT;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(playerUsername);
        buf.writeString(serviceName);
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUsername = buf.readString();
        serviceName = buf.readString();
        ;
    }
}
