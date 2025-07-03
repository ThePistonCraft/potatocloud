package net.potatocloud.manager.group;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.service.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ServiceGroupManagerImpl implements ServiceGroupManager {

    private final List<ServiceGroup> serviceGroups = new ArrayList<>();
    private final Path groupsPath;

    @Override
    public ServiceGroup getServiceGroup(String groupName) {
        return serviceGroups.stream()
                .filter(cloudServiceGroup -> cloudServiceGroup.getName().equalsIgnoreCase(groupName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ServiceGroup> getAllServiceGroups() {
        return serviceGroups;
    }

    @Override
    public ServiceGroup createServiceGroup(
            String name,
            int minOnlineCount,
            int maxOnlineCount,
            int maxPlayers,
            int maxMemory,
            boolean fallback,
            boolean isStatic,
            Platform platform
    ) {
        final List<String> templates = new ArrayList<>();
        templates.add("every");
        templates.add(name);
        //todo
        /*
        if (platform.isProxy()) {
            templates.add("every_proxy");
        } else {
            templates.add("every_service");
        }
         */
        final ServiceGroup group = new ServiceGroupImpl(
                name,
                minOnlineCount,
                maxOnlineCount,
                maxPlayers,
                maxMemory,
                fallback,
                isStatic,
                platform,
                templates);
        serviceGroups.add(group);
        return group;
    }

    @Override
    public boolean deleteServiceGroup(ServiceGroup group) {
        if (group == null || !serviceGroups.contains(group)) {
            return false;
        }

        for (Service service : group.getOnlineServices()) {
            service.shutdown();
        }

        serviceGroups.remove(group);

        final Path filePath = groupsPath.resolve(group.getName() + ".yml");
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
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

        Files.list(groupsPath).filter(path -> path.toString().endsWith(".yml")).forEach(path -> {
            ServiceGroupImpl group = ServiceGroupImpl.loadFromFile(path.toFile());
            serviceGroups.add(group);
        });
    }
}
