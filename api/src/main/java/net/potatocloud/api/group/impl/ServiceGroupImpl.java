package net.potatocloud.api.group.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersions;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ServiceGroupImpl implements ServiceGroup {

    private final String name;
    private int minOnlineCount;
    private int maxOnlineCount;
    private int maxPlayers;
    private int maxMemory;
    private boolean fallback;
    private boolean isStatic;
    private final String platformName;
    private final List<String> serviceTemplates;

    @Override
    public Platform getPlatform() {
        return PlatformVersions.getPlatformByName(platformName);
    }

    @Override
    public void addServiceTemplate(String template) {
        serviceTemplates.add(template);
    }

    @Override
    public void removeServiceTemplate(String template) {
        serviceTemplates.remove(template);
    }

    @Override
    public List<Service> getOnlineServices() {
        return CloudAPI.getInstance()
                .getServiceManager()
                .getAllOnlineServices()
                .stream()
                .filter(service -> service.getServiceGroup().getName().equals(name))
                .filter(service -> service.getState().equals(ServiceState.RUNNING) || service.getState().equals(ServiceState.STARTING))
                .toList();
    }

    @Override
    public int getOnlineServiceCount() {
        return getOnlineServices().size();
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getServiceGroupManager().updateServiceGroup(this);
    }
}
