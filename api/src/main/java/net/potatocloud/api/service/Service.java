package net.potatocloud.api.service;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.property.PropertyHolder;

public interface Service extends PropertyHolder {

    String getName();

    int getServiceId();

    boolean isOnline();

    ServiceStatus getStatus();

    void setStatus(ServiceStatus status);

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
