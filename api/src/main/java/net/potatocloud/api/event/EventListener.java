package net.potatocloud.api.event;

public interface EventListener<T extends Event> {

    void onEvent(T event);

}
