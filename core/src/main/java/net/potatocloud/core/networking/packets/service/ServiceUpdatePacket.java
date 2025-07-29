package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.netty.PacketBuffer;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceUpdatePacket implements Packet {

    private String serviceName;
    private String status;
    private int maxPlayers;
    private Set<PropertyData> properties;

    @Override
    public int getId() {
        return PacketIds.SERVICE_UPDATE;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(serviceName);
        buf.writeString(status);
        buf.writeInt(maxPlayers);
        buf.writePropertyDataSet(properties);
    }

    @Override
    public void read(PacketBuffer buf) {
        serviceName = buf.readString();
        status = buf.readString();
        maxPlayers = buf.readInt();
        properties = buf.readPropertyDataSet();
    }
}
