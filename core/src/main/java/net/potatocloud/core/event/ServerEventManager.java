package net.potatocloud.core.event;

import net.potatocloud.api.event.Event;
import net.potatocloud.api.event.EventListener;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.core.networking.NetworkClient;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.EventPacket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerEventManager implements EventManager {

    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = new HashMap<>();
    private final NetworkServer server;

    public ServerEventManager(NetworkServer server) {
        this.server = server;

        server.registerPacketListener(PacketTypes.EVENT, (NetworkConnection connection, EventPacket packet) -> {
            Event event = EventSerializer.deserialize(packet);
            if (event != null) {
                callLocal(event);
            }
        });
    }

    public <T extends Event> void on(Class<T> eventClass, EventListener<T> listener) {
        listeners.computeIfAbsent(eventClass, key -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void callLocal(T event) {
        for (EventListener<?> listener : listeners.get(event.getClass())) {
            ((EventListener<T>) listener).onEvent(event);
        }
    }


    @SuppressWarnings("unchecked")
    public <T extends Event> void call(T event) {
        callLocal(event);

        // send the evet packet to call listeners in other jvm processes as well
        EventPacket packet = EventSerializer.serialize(event);
        server.broadcastPacket(packet);
    }
}
