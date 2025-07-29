package net.potatocloud.core.event;

import net.potatocloud.api.event.Event;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.packets.EventPacket;

public class ServerEventManager extends BaseEventManager {

    private final NetworkServer server;

    public ServerEventManager(NetworkServer server) {
        this.server = server;

        server.registerPacketListener(PacketIds.EVENT, (NetworkConnection connection, EventPacket packet) -> {
            final Event event = EventSerializer.deserialize(packet);
            if (event != null) {
                callLocal(event);
            }
        });
    }

    @Override
    public <T extends Event> void call(T event) {
        callLocal(event);
        server.broadcastPacket(EventSerializer.serialize(event));
    }
}
