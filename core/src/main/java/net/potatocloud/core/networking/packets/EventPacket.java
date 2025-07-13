package net.potatocloud.core.networking.packets;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class EventPacket implements Packet {

    private String eventClass;
    private String json;

    @Override
    public String getType() {
        return PacketTypes.EVENT;
    }
}
