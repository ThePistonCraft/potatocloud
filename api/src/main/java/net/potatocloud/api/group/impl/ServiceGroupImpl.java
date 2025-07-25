package net.potatocloud.api.group.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersions;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<String> customJvmFlags;
    private final Set<Property> properties;

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
        customJvmFlags = new ArrayList<>();
        properties = new HashSet<>();
    }

    @Override
    public String getPropertyHolderName() {
        return getName();
    }

    @Override
    public void setProperty(Property property, Object value) {
        ServiceGroup.super.setProperty(property, value);

        final Property prop = getProperty(property.getName());
        if (prop != null) {
            for (Service onlineService : getAllServices()) {
                onlineService.setProperty(prop, prop.getValue());
            }
        }
    }

    @Override
    public Platform getPlatform() {
        return PlatformVersions.getPlatformByName(platformName);
    }

    @Override
    public void addCustomJvmFlag(String flag) {
        customJvmFlags.add(flag);
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
    public int getOnlineServiceCount() {
        return getOnlineServices().size();
    }

    @Override
    public void update() {
        CloudAPI.getInstance().getServiceGroupManager().updateServiceGroup(this);
    }
}
