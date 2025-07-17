package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class StartServicePacket implements Packet {

    private String groupName;

    @Override
    public String getType() {
        return PacketTypes.START_SERVICE;
    }
}
