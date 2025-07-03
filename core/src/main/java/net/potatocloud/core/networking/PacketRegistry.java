package net.potatocloud.core.networking;

import net.potatocloud.core.networking.packets.MessagePacket;

public class PacketRegistry {

    public static void registerPackets(PacketManager manager) {
        manager.register(PacketTypes.MESSAGE, MessagePacket.class);
    }
}
