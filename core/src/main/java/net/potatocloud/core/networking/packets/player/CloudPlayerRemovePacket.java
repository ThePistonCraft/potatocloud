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
public class CloudPlayerRemovePacket implements Packet {

    private UUID playerUniqueId;

    @Override
    public int getId() {
        return PacketIds.PLAYER_REMOVE;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(playerUniqueId.toString());
    }

    @Override
    public void read(PacketBuffer buf) {
        playerUniqueId = UUID.fromString(buf.readString());
    }
}
