package net.potatocloud.api.service;

import net.potatocloud.api.group.ServiceGroup;

public interface Service {

    String getName();

    int getServiceId();

    boolean isOnline();

    ServiceState getState();

    void setState(ServiceState state);

    long getStartTimestamp();

    int getOnlinePlayers();

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    int getUsedMemory();

    int getPort();

    ServiceGroup getServiceGroup();

    void shutdown();

    boolean executeCommand(String command);

    void update();
}
