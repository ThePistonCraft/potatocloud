package net.potatocloud.api.service;

import net.potatocloud.api.group.ServiceGroup;

import java.util.List;

public interface ServiceManager {

    Service getService(String serviceName);

    List<Service> getAllOnlineServices();

    void startService(ServiceGroup group);

    void startServices(ServiceGroup group, int count);

}
