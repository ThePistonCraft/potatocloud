package net.potatocloud.core.networking.packets.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateGroupPacket implements Packet {

    private String groupName;
    private int minOnlineCount;
    private int maxOnlineCount;
    private int maxPlayers;
    private int maxMemory;
    private boolean fallback;
    private List<String> serviceTemplates;

    @Override
    public String getType() {
        return PacketTypes.UPDATE_GROUP;
    }
}
