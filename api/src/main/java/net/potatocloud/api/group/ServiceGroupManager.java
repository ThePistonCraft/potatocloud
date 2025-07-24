package net.potatocloud.api.group;

import java.util.List;

public interface ServiceGroupManager {

    ServiceGroup getServiceGroup(String name);

    List<ServiceGroup> getAllServiceGroups();

    ServiceGroup createServiceGroup(String name, String platformName, int minOnlineCount, int maxOnlineCount, int maxPlayers, int maxMemory, boolean fallback, boolean isStatic);

    void deleteServiceGroup(String name);

    default void deleteServiceGroup(ServiceGroup group) {
        deleteServiceGroup(group.getName());
    }

    void updateServiceGroup(ServiceGroup group);

    boolean existsServiceGroup(String name);

}
