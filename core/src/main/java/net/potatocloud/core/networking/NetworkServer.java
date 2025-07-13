package net.potatocloud.core.networking;

import java.util.List;

public interface NetworkServer {

    void start(String hostname, int port);

    void shutdown();

    boolean isRunning();

    <T extends Packet> void registerPacketListener(String packetType, PacketListener<T> listener);

    List<NetworkConnection> getConnectedSessions();

    int getPort();

    void sendToClient(NetworkConnection client, Packet packet);

    default void broadcastPacket(Packet packet) {
        getConnectedSessions().forEach(connectedSession -> sendToClient(connectedSession, packet));
    }
}
