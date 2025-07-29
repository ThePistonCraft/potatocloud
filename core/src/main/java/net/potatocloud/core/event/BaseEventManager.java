package net.potatocloud.core.event;

import net.potatocloud.api.event.Event;
import net.potatocloud.api.event.EventListener;
import net.potatocloud.api.event.EventManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class BaseEventManager implements EventManager {

    protected final Map<Class<? extends Event>, List<EventListener<?>>> listeners = new ConcurrentHashMap<>();

    @Override
    public <T extends Event> void on(Class<T> eventClass, EventListener<T> listener) {
        listeners.computeIfAbsent(eventClass, key -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void callLocal(T event) {
        final List<EventListener<?>> eventListenerList = listeners.get(event.getClass());
        if (eventListenerList == null) {
            return;
        }

        for (EventListener<?> e : eventListenerList) {
            final EventListener<T> listener = (EventListener<T>) e;
            listener.onEvent(event);
        }
    }
}
