package net.potatocloud.api.group;

import net.potatocloud.api.platform.Platform;

import java.util.List;

public interface ServiceGroupManager {

    ServiceGroup getServiceGroup(String name);

    List<ServiceGroup> getAllServiceGroups();

    ServiceGroup createServiceGroup(String name, int minOnlineCount, int maxOnlineCount, int maxPlayers, int maxMemory, boolean fallback, boolean isStatic, String platformName);

    boolean deleteServiceGroup(ServiceGroup group);

    void updateServiceGroup(ServiceGroup group);

    boolean existsServiceGroup(String name);

}
