package net.potatocloud.core.networking;

public interface PacketListener<T extends Packet> {

    void onPacket(NetworkConnection connection, T packet);

}
