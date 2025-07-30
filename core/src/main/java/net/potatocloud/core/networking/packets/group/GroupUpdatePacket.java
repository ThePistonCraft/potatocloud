package net.potatocloud.core.networking.packets.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.api.property.Property;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.netty.PacketBuffer;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdatePacket implements Packet {

    private String groupName;
    private int minOnlineCount;
    private int maxOnlineCount;
    private int maxPlayers;
    private int maxMemory;
    private boolean fallback;
    private List<String> serviceTemplates;
    private Set<Property> properties;
    private List<String> customJvmFlags;

    @Override
    public int getId() {
        return PacketIds.GROUP_UPDATE;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(groupName);
        buf.writeInt(minOnlineCount);
        buf.writeInt(maxOnlineCount);
        buf.writeInt(maxPlayers);
        buf.writeInt(maxMemory);
        buf.writeBoolean(fallback);
        buf.writeStringList(serviceTemplates);
        buf.writePropertySet(properties);
        buf.writeStringList(customJvmFlags);
    }

    @Override
    public void read(PacketBuffer buf) {
        groupName = buf.readString();
        minOnlineCount = buf.readInt();
        maxOnlineCount = buf.readInt();
        maxPlayers = buf.readInt();
        maxMemory = buf.readInt();
        fallback = buf.readBoolean();
        serviceTemplates = buf.readStringList();
        properties = buf.readPropertySet();
        customJvmFlags = buf.readStringList();
    }
}
