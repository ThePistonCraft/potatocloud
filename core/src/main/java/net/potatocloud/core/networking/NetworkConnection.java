package net.potatocloud.core.networking;

public interface NetworkConnection {

    void send(Packet packet);

    void close();

}
