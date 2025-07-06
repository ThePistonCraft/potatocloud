package net.potatocloud.api.group;

import net.potatocloud.api.platform.Platform;

import java.util.List;

public interface ServiceGroupManager {

    ServiceGroup getServiceGroup(String groupName);

    List<ServiceGroup> getAllServiceGroups();

    ServiceGroup createServiceGroup(String name, int minOnlineCount, int maxOnlineCount, int maxPlayers, int maxMemory, boolean fallback, boolean isStatic, Platform platform);

    boolean deleteServiceGroup(ServiceGroup serviceGroup);

    boolean existsServiceGroup(String groupName);

}
