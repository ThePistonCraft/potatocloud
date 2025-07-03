package net.potatocloud.core.networking;

public interface PacketListener<T extends Packet> {

    void onPacket(NetworkConnection session, T packet);

}
