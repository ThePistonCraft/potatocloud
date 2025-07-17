package net.potatocloud.core.networking.packets.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AddCloudPlayerPacket implements Packet {

    private String username;
    private UUID uniqueId;
    private String connectedProxyName;

    @Override
    public String getType() {
        return PacketTypes.PLAYER_ADD;
    }
}
