package net.potatocloud.node.group;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import org.simpleyaml.configuration.file.YamlFile;

import java.nio.file.Path;

@UtilityClass
public class ServiceGroupStorage {

    @SneakyThrows
    public void saveToFile(ServiceGroup group, Path directory) {
        final YamlFile config = new YamlFile(directory.resolve(group.getName() + ".yml").toFile());
        config.set("name", group.getName());
        config.set("platform", group.getPlatform().getFullName());
        config.set("templates", group.getServiceTemplates());
        config.set("minOnlineCount", group.getMinOnlineCount());
        config.set("maxOnlineCount", group.getMaxOnlineCount());
        config.set("maxPlayers", group.getMaxPlayers());
        config.set("maxMemory", group.getMaxMemory());
        config.set("fallback", group.isFallback());
        config.set("static", group.isStatic());
        config.set("startPriority", group.getStartPriority());
        config.set("startPercentage", group.getStartPercentage());
        config.set("javaCommand", group.getJavaCommand());
        config.save();
    }

    @SneakyThrows
    public ServiceGroup loadFromFile(Path groupFile) {
        final YamlFile config = new YamlFile(groupFile.toFile());
        config.load();

        return new ServiceGroupImpl(
                config.getString("name"),
                config.getString("platform"),
                config.getStringList("templates"),
                config.getInt("minOnlineCount"),
                config.getInt("maxOnlineCount"),
                config.getInt("maxPlayers"),
                config.getInt("maxMemory"),
                config.getBoolean("fallback"),
                config.getBoolean("static"),
                config.getInt("startPriority"),
                config.getInt("startPercentage"),
                config.getString("javaCommand")
        );
    }
}
