package net.potatocloud.manager.group;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.service.Service;
import net.potatocloud.manager.Manager;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ServiceGroupImpl implements ServiceGroup {

    private final Path configPath;
    private final YamlFile config;

    private final String name;
    private final Platform platform;
    private final List<String> serviceTemplates;
    private int minOnlineCount;
    private int maxOnlineCount;
    private int maxPlayers;
    private int maxMemory;
    private boolean fallback;
    private boolean isStatic;

    public ServiceGroupImpl(
            String name,
            int minOnlineCount,
            int maxOnlineCount,
            int maxPlayers,
            int maxMemory,
            boolean fallback,
            boolean isStatic,
            Platform platform,
            List<String> serviceTemplates
    ) {
        this.name = name;

        this.minOnlineCount = minOnlineCount;
        this.maxOnlineCount = maxOnlineCount;
        this.maxPlayers = maxPlayers;
        this.maxMemory = maxMemory;
        this.fallback = fallback;
        this.isStatic = isStatic;
        this.platform = platform;
        this.serviceTemplates = new ArrayList<>(serviceTemplates);

        final Path groupsFolder = Path.of(Manager.getInstance().getConfig().getGroupsFolder());

        this.configPath = groupsFolder.resolve(name + ".yml");
        groupsFolder.toFile().mkdirs();

        // create template folders
        for (String templateName : serviceTemplates) {
            //todo
        }

        this.config = new YamlFile(configPath.toFile());

        save();
    }

    @SneakyThrows
    public static ServiceGroupImpl loadFromFile(File file) {
        final YamlFile config = new YamlFile(file);
        config.load();

        //todo read platform
        return new ServiceGroupImpl(
                config.getString("name"),
                config.getInt("minOnlineCount"),
                config.getInt("maxOnlineCount"),
                config.getInt("maxPlayers"),
                config.getInt("maxMemory"),
                config.getBoolean("fallback"),
                config.getBoolean("static"),
                null,
                config.getStringList("templates")
        );
    }

    @Override
    public List<Service> getOnlineServices() {
        return CloudAPI.getInstance()
                .getServiceManager()
                .getAllOnlineServices()
                .stream()
                .filter(service -> service.getServiceGroup().getName().equals(name))
                .filter(Service::isOnline)
                .toList();
    }

    @Override
    public int getOnlineServiceCount() {
        return getOnlineServices().size();
    }

    @Override
    public void update() {
        save();
    }

    public void addServiceTemplate(String template) {
        if (!serviceTemplates.contains(template)) {
            serviceTemplates.add(template);
        }
    }

    public void removeServiceTemplate(String template) {
        serviceTemplates.remove(template);
    }

    @SneakyThrows
    public void save() {
        config.set("name", name);
        config.set("minOnlineCount", minOnlineCount);
        config.set("maxOnlineCount", maxOnlineCount);
        config.set("maxPlayers", maxPlayers);
        config.set("maxMemory", maxMemory);
        config.set("fallback", fallback);
        config.set("static", isStatic);
        // todo config.set("platform", platform.getName());
        config.set("templates", serviceTemplates);
        config.save();
    }
}
