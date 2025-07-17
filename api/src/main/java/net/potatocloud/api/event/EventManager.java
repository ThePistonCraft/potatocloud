package net.potatocloud.api.event;

public interface EventManager {

    <T extends Event> void on(Class<T> eventClass, EventListener<T> listener);

    <T extends Event> void callLocal(T event);

    <T extends Event> void call(T event);

}
