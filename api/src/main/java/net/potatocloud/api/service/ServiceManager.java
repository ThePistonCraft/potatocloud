package net.potatocloud.api.service;

import net.potatocloud.api.group.ServiceGroup;

import java.util.List;

public interface ServiceManager {

    Service getService(String serviceName);

    List<Service> getAllServices();

    void updateService(Service service);

    void startService(String groupName);

    default void startService(ServiceGroup group) {
        startService(group.getName());
    }

    void startServices(String groupName, int count);

    default void startServices(ServiceGroup group, int count) {
        startServices(group.getName(), count);
    }
}
