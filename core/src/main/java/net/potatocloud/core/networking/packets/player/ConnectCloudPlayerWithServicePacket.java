package net.potatocloud.core.networking.packets.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class ConnectCloudPlayerWithServicePacket implements Packet {

    private String playerUsername;
    private String serviceName;

    @Override
    public String getType() {
        return PacketTypes.CONNECT_PLAYER;
    }
}
