package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.netty.PacketBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartServicePacket implements Packet {

    private String groupName;

    @Override
    public int getId() {
        return PacketIds.START_SERVICE;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(groupName);
    }

    @Override
    public void read(PacketBuffer buf) {
        groupName = buf.readString();
    }
}
