package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class ServiceRemovePacket implements Packet {

    private String serviceName;
    private int servicePort;

    @Override
    public String getType() {
        return PacketTypes.SERVICE_REMOVE;
    }
}
