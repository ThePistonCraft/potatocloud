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
    private final String platformName;
    private final List<String> serviceTemplates;
    private int minOnlineCount;
    private int maxOnlineCount;
    private int maxPlayers;
    private int maxMemory;
    private boolean fallback;
    private boolean isStatic;
    private int startPriority;
    private int startPercentage;
    private String javaCommand;

    public ServiceGroupImpl(String name, String platformName, List<String> serviceTemplates, int minOnlineCount, int maxOnlineCount, int maxPlayers, int maxMemory, boolean fallback, boolean isStatic) {
        this.name = name;
        this.platformName = platformName;
        this.serviceTemplates = serviceTemplates;
        this.minOnlineCount = minOnlineCount;
        this.maxOnlineCount = maxOnlineCount;
        this.maxPlayers = maxPlayers;
        this.maxMemory = maxMemory;
        this.fallback = fallback;
        this.isStatic = isStatic;

        startPriority = 0;
        startPercentage = 100;
        javaCommand = "java";
    }

    @Override
    public Platform getPlatform() {
        return PlatformVersions.getPlatformByName(platformName);
    }

    @Override
    public void addServiceTemplate(String template) {
        if (serviceTemplates.contains(template)) {
            return;
        }
        serviceTemplates.add(template);
    }

    @Override
    public void removeServiceTemplate(String template) {
        if (!serviceTemplates.contains(template)) {
            return;
        }
        serviceTemplates.remove(template);
    }

    @Override
    public List<Service> getOnlineServices() {
        return CloudAPI.getInstance()
                .getServiceManager()
                .getAllServices()
                .stream()
                .filter(service -> service.getServiceGroup().getName().equals(name))
                .filter(service -> service.getState().equals(ServiceState.RUNNING))
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
