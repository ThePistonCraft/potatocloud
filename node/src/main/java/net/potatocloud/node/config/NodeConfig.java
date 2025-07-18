package net.potatocloud.node.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.simpleyaml.configuration.comments.format.YamlCommentFormat;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;

@Getter
public class NodeConfig {

    private int serviceStartPort = 30000;
    private int proxyStartPort = 25565;

    private String prompt = "&7&a%user%&7@cloud ~> ";
    private String splitter = "-";
    private boolean platformAutoUpdate = true;

    private String groupsFolder = "groups";
    private String staticFolder = "services/static";
    private String tempServicesFolder = "services/temp";
    private String templatesFolder = "templates";
    private String platformsFolder = "platforms";
    private String logsFolder = "logs";
    private String dataFolder = "data";

    private String nodeHost = "127.0.0.1";
    private int nodePort = 9000;

    @SneakyThrows
    public NodeConfig() {
        final File file = new File("config.yml");
        final YamlFile yaml = new YamlFile(file);

        if (!file.exists()) {
            file.createNewFile();
            save(yaml);
        }

        yaml.load();

        serviceStartPort = yaml.getInt("service-start-port", serviceStartPort);
        proxyStartPort = yaml.getInt("proxy-start-port", proxyStartPort);
        prompt = yaml.getString("prompt", prompt);
        splitter = yaml.getString("splitter", splitter);
        platformAutoUpdate = yaml.getBoolean("platformAutoUpdate", platformAutoUpdate);

        groupsFolder = yaml.getString("folders.groups", groupsFolder);
        staticFolder = yaml.getString("folders.static", staticFolder);
        tempServicesFolder = yaml.getString("folders.temp-services", tempServicesFolder);
        templatesFolder = yaml.getString("folders.templates", templatesFolder);
        platformsFolder = yaml.getString("folders.platforms", platformsFolder);
        logsFolder = yaml.getString("folders.logs", logsFolder);
        dataFolder = yaml.getString("folders.data", dataFolder);

        nodeHost = yaml.getString("node.host", nodeHost);
        nodePort = yaml.getInt("node.port", nodePort);
    }

    @SneakyThrows
    private void save(YamlFile yaml) {
        yaml.setCommentFormat(YamlCommentFormat.PRETTY);

        yaml.set("service-start-port", serviceStartPort);
        yaml.set("proxy-start-port", proxyStartPort);
        yaml.setComment("prompt", "Console Prompt (placeholders: %user%)");
        yaml.set("prompt", prompt);
        yaml.setComment("splitter", "Separator between name and id of a service");
        yaml.set("splitter", splitter);
        yaml.set("platformAutoUpdate", platformAutoUpdate);

        yaml.set("folders.groups", groupsFolder);
        yaml.set("folders.static", staticFolder);
        yaml.set("folders.temp-services", tempServicesFolder);
        yaml.set("folders.templates", templatesFolder);
        yaml.set("folders.platforms", platformsFolder);
        yaml.set("folders.logs", logsFolder);
        yaml.set("folders.data", dataFolder);

        yaml.set("node.host", nodeHost);
        yaml.set("node.port", nodePort);

        yaml.save();
    }
}
