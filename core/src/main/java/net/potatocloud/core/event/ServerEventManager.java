package net.potatocloud.core.event;

import net.potatocloud.api.event.Event;
import net.potatocloud.api.event.EventListener;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.packets.EventPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerEventManager implements EventManager {

    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = new HashMap<>();
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

    public <T extends Event> void on(Class<T> eventClass, EventListener<T> listener) {
        final List<EventListener<? extends Event>> eventListeners = listeners.computeIfAbsent(eventClass, k -> new ArrayList<>());
        eventListeners.add(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void callLocal(T event) {
        final List<EventListener<? extends Event>> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null || eventListeners.isEmpty()) {
            return;
        }
        for (EventListener<?> listener : eventListeners) {
            ((EventListener<T>) listener).onEvent(event);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void call(T event) {
        callLocal(event);
        final EventPacket packet = EventSerializer.serialize(event);
        server.broadcastPacket(packet);
    }
}
