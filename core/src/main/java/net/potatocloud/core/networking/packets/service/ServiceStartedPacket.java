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
public class ServiceStartedPacket implements Packet {

    private String serviceName;

    @Override
    public int getId() {
        return PacketIds.SERVICE_STARTED;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(serviceName);
    }

    @Override
    public void read(PacketBuffer buf) {
        serviceName = buf.readString();
    }
}
