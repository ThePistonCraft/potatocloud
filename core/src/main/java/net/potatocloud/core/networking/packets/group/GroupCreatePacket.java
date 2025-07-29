package net.potatocloud.core.networking.packets.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.netty.PacketBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreatePacket implements Packet {

    private String name;
    private String platformName;
    private int minOnlineCount;
    private int maxOnlineCount;
    private int maxPlayers;
    private int maxMemory;
    private boolean fallback;
    private boolean isStatic;

    @Override
    public int getId() {
        return PacketIds.GROUP_CREATE;
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(name);
        buf.writeString(platformName);
        buf.writeInt(minOnlineCount);
        buf.writeInt(maxOnlineCount);
        buf.writeInt(maxPlayers);
        buf.writeInt(maxMemory);
        buf.writeBoolean(fallback);
        buf.writeBoolean(isStatic);
    }

    @Override
    public void read(PacketBuffer buf) {
        name = buf.readString();
        platformName = buf.readString();
        minOnlineCount = buf.readInt();
        maxOnlineCount = buf.readInt();
        maxPlayers = buf.readInt();
        maxMemory = buf.readInt();
        fallback = buf.readBoolean();
        isStatic = buf.readBoolean();
    }
}
