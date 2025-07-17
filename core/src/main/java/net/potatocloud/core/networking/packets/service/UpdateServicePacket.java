package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class UpdateServicePacket implements Packet {

    private String serviceName;
    private String stateName;
    private int maxPlayers;

    @Override
    public String getType() {
        return PacketTypes.UPDATE_SERVICE;
    }
}
