package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class ServiceAddPacket implements Packet {

    private final String name;
    private final int serviceId;
    private final int port;
    private final long startTimestamp;
    private final String groupName;

    private String status;
    private int onlinePlayers;
    private int usedMemory;

    @Override
    public String getType() {
        return PacketTypes.SERVICE_ADD;
    }
}
