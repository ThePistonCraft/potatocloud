package net.potatocloud.core.networking;

public interface NetworkClient {

    void connect(String host, int port);

    void send(Packet packet);

    void disconnect();

    <T extends Packet> void registerPacketListener(String packetType, PacketListener<T> listener);

}
