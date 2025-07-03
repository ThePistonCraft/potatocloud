package net.potatocloud.manager.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.simpleyaml.configuration.comments.format.YamlCommentFormat;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;

@Getter
public class ManagerConfig {

    private int serviceStartPort = 30000;
    private int proxyStartPort = 25565;

    private String prompt = "%user%@manager &8> ";
    private String splitter = "-";

    private String groupsFolder = "groups";
    private String staticFolder = "services/static";
    private String tempServicesFolder = "services/temp";
    private String templatesFolder = "templates";
    private String platformsFolder = "platforms";
    private String logsFolder = "logs";

    private String controllerHost = "127.0.0.1";
    private int controllerPort = 9000;

    @SneakyThrows
    public ManagerConfig() {
        final File file = new File("config.yml");
        final YamlFile yaml = new YamlFile(file);

        if (!file.exists()) {
            file.createNewFile();
            save(yaml);
        }

        yaml.load();

        this.serviceStartPort = yaml.getInt("service-start-port", serviceStartPort);
        this.proxyStartPort = yaml.getInt("proxy-start-port", proxyStartPort);
        this.prompt = yaml.getString("prompt", prompt);
        this.splitter = yaml.getString("splitter", splitter);

        this.groupsFolder = yaml.getString("folders.groups", groupsFolder);
        this.staticFolder = yaml.getString("folders.static", staticFolder);
        this.tempServicesFolder = yaml.getString("folders.temp-services", tempServicesFolder);
        this.templatesFolder = yaml.getString("folders.templates", templatesFolder);
        this.platformsFolder = yaml.getString("folders.platforms", platformsFolder);
        this.logsFolder = yaml.getString("folders.logs", logsFolder);

        this.controllerHost = yaml.getString("manager.host", controllerHost);
        this.controllerPort = yaml.getInt("manager.port", controllerPort);
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

        yaml.set("folders.groups", groupsFolder);
        yaml.set("folders.static", staticFolder);
        yaml.set("folders.temp-services", tempServicesFolder);
        yaml.set("folders.templates", templatesFolder);
        yaml.set("folders.platforms", platformsFolder);
        yaml.set("folders.logs", logsFolder);

        yaml.set("manager.host", controllerHost);
        yaml.set("manager.port", controllerPort);

        yaml.save();
    }
}
