package net.potatocloud.core.networking;

import net.potatocloud.core.networking.packets.*;
import net.potatocloud.core.networking.packets.service.*;
import net.potatocloud.core.networking.packets.group.*;

public class PacketRegistry {

    public static void registerPackets(PacketManager manager) {
        manager.register(PacketTypes.MESSAGE, MessagePacket.class);
        manager.register(PacketTypes.SERVICE_ADD, ServiceAddPacket.class);
        manager.register(PacketTypes.SERVICE_REMOVE, ServiceRemovePacket.class);
        manager.register(PacketTypes.SERVICE_STARTED, ServiceStartedPacket.class);
        manager.register(PacketTypes.REQUEST_SERVICES, RequestServicesPacket.class);
        manager.register(PacketTypes.REQUEST_GROUPS, RequestGroupsPacket.class);
        manager.register(PacketTypes.GROUP_ADD, AddGroupPacket.class);
        manager.register(PacketTypes.EVENT, EventPacket.class);
    }
}
