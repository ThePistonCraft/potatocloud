package net.potatocloud.api.service;

import net.potatocloud.api.group.ServiceGroup;

public interface Service {

    String getName();

    int getServiceId();

    boolean isOnline();

    ServiceState getState();

    long getStartTimestamp();

    int getOnlinePlayers();

    int getUsedMemory();

    String getHost();

    int getPort();

    ServiceGroup getServiceGroup();

    void shutdown();

    boolean executeCommand(String command);

}
