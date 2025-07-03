package net.potatocloud.core.networking.packets;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class MessagePacket implements Packet {

    private String message;

    @Override
    public String getType() {
        return PacketTypes.MESSAGE;
    }
}
