package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class ServiceStartedPacket implements Packet {

    private String serviceName;

    @Override
    public String getType() {
        return PacketTypes.SERVICE_STARTED;
    }
}
