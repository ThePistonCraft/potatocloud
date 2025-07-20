package net.potatocloud.core.networking.packets.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

import java.util.List;
import java.util.Set;

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
    private Set<PropertyData> properties;
    private List<String> customJvmFlags;

    @Override
    public String getType() {
        return PacketTypes.UPDATE_GROUP;
    }
}
