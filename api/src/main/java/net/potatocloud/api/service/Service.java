package net.potatocloud.api.service;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.property.PropertyHolder;

import java.util.List;
import java.util.Set;

public interface Service extends PropertyHolder {

    String getName();

    int getServiceId();

    boolean isOnline();

    ServiceStatus getStatus();

    void setStatus(ServiceStatus status);

    long getStartTimestamp();

    Set<CloudPlayer> getOnlinePlayers();

    int getOnlinePlayersCount();

    int getMaxPlayers();

    void setMaxPlayers(int maxPlayers);

    int getUsedMemory();

    int getPort();

    ServiceGroup getServiceGroup();

    void shutdown();

    boolean executeCommand(String command);

    void copy(String template, String filter);

    default void copy(String template) {
        copy(template, "");
    }

    default void update() {
        CloudAPI.getInstance().getServiceManager().updateService(this);
    }
}
