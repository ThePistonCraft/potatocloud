package net.potatocloud.core.networking.packets.service;

import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

public class RequestServicesPacket implements Packet {

    @Override
    public String getType() {
        return PacketTypes.REQUEST_SERVICES;
    }
}
