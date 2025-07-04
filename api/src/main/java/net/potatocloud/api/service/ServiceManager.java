package net.potatocloud.api.service;

import net.potatocloud.api.group.ServiceGroup;

import java.util.List;

public interface ServiceManager {

    Service getService(String serviceName);

    List<Service> getAllOnlineServices();

    void startService(ServiceGroup serviceGroup);

    void startServices(ServiceGroup serviceGroup, int count);

}
