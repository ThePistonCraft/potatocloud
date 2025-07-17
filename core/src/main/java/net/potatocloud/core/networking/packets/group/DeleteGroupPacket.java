package net.potatocloud.core.networking.packets.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

@Data
@AllArgsConstructor
public class DeleteGroupPacket implements Packet {

    private String groupName;

    @Override
    public String getType() {
        return PacketTypes.DELETE_GROUP;
    }
}
