package net.potatocloud.node.group;

import lombok.SneakyThrows;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersions;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.group.AddGroupPacket;
import net.potatocloud.core.networking.packets.group.UpdateGroupPacket;
import net.potatocloud.node.Node;
import net.potatocloud.node.listeners.group.RequestGroupsListener;
import net.potatocloud.node.listeners.group.UpdateGroupListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> serviceGroups = new ArrayList<>();
    private final Path groupsPath;
    private final NetworkServer server;

    public ServiceGroupManagerImpl(Path groupsPath, NetworkServer server) {
        this.groupsPath = groupsPath;
        this.server = server;

        server.registerPacketListener(PacketTypes.REQUEST_GROUPS, new RequestGroupsListener(this));
        server.registerPacketListener(PacketTypes.UPDATE_GROUP, new UpdateGroupListener(this));
    }

    @Override
    public ServiceGroup getServiceGroup(String name) {
        return serviceGroups.stream()
                .filter(cloudServiceGroup -> cloudServiceGroup.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        return Collections.unmodifiableList(serviceGroups);
    }

    @Override
    public ServiceGroup createServiceGroup(
            String name,
            String platformName,
            int minOnlineCount,
            int maxOnlineCount,
            int maxPlayers,
            int maxMemory,
            boolean fallback,
            boolean isStatic
    ) {
        final List<String> templates = new ArrayList<>();
        templates.add("every");
        templates.add(name);

        final Platform platform = PlatformVersions.getPlatformByName(platformName);

        if (platform.isProxy()) {
            templates.add("every_proxy");
        } else {
            templates.add("every_service");
        }

        for (String templateName : templates) {
            Node.getInstance().getTemplateManager().createTemplate(templateName);
        }

        final ServiceGroup serviceGroup = new ServiceGroupImpl(
                name,
                platformName,
                templates,
                minOnlineCount,
                maxOnlineCount,
                maxPlayers,
                maxMemory,
                fallback,
                isStatic
        );

        // send group add packet to clients
        server.broadcastPacket(new AddGroupPacket(
                name,
                minOnlineCount,
                maxOnlineCount,
                maxPlayers,
                maxMemory,
                fallback,
                isStatic,
                platformName,
                templates
        ));

        ServiceGroupStorage.saveToFile(serviceGroup, groupsPath);
        serviceGroups.add(serviceGroup);
        return serviceGroup;
    }

    @Override
    public boolean deleteServiceGroup(ServiceGroup serviceGroup) {
        if (serviceGroup == null || !serviceGroups.contains(serviceGroup)) {
            return false;
        }

        serviceGroup.getOnlineServices().forEach(Service::shutdown);

        serviceGroups.remove(serviceGroup);

        final Path filePath = groupsPath.resolve(serviceGroup.getName() + ".yml");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void updateServiceGroup(ServiceGroup group) {
        ServiceGroupStorage.saveToFile(group, groupsPath);

        server.broadcastPacket(new UpdateGroupPacket(
                group.getName(),
                group.getMinOnlineCount(),
                group.getMaxOnlineCount(),
                group.getMaxPlayers(),
                group.getMaxMemory(),
                group.isFallback(),
                group.getServiceTemplates()
        ));
    }

    @Override
    public boolean existsServiceGroup(String groupName) {
        if (groupName == null) {
            return false;
        }
        return serviceGroups.stream().anyMatch(serviceGroup -> serviceGroup != null && serviceGroup.getName().equalsIgnoreCase(groupName));
    }


    @SneakyThrows
    public void loadGroups() {
        if (!Files.exists((groupsPath))) {
            return;
        }

        try (Stream<Path> paths = Files.list(groupsPath)) {
            paths.filter(path -> path.toString().endsWith(".yml")).forEach(path -> serviceGroups.add(ServiceGroupStorage.loadFromFile(path)));
        }
    }
}
