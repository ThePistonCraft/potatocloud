package net.potatocloud.core.networking.packets.group;

import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

public class RequestGroupsPacket implements Packet {

    @Override
    public String getType() {
        return PacketTypes.REQUEST_GROUPS;
    }
}
