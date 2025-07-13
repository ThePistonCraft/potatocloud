package net.potatocloud.node.command.commands;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.PlatformVersions;
import net.potatocloud.node.command.Command;
import net.potatocloud.node.console.Logger;
import net.potatocloud.node.platform.PlatformManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
public class PlatformCommand implements Command {

    private final Logger logger;
    private final Path platformsFolder;
    private final PlatformManager platformManager;

    @SneakyThrows
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            sendHelp();
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "list" -> listPlatforms();
            case "download" -> downloadPlatform(args);
            case "remove" -> removePlatform(args);
            case "list-downloaded" -> listDownloaded();
            default -> {
                sendHelp();
            }
        }
    }

    private void listPlatforms() {
        logger.info("&7Available platforms:");
        for (PlatformVersions platform : PlatformVersions.values()) {
            logger.info("&8» &a" + platform.platform().getFullName() + " &7- Proxy: &a" + platform.platform().isProxy());
        }
    }

    @SneakyThrows
    private void downloadPlatform(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: &7platform download &8[&aname&8]");
            return;
        }

        platformManager.downloadPlatform(PlatformVersions.getPlatformByName(args[1]));
    }
    
    private void removePlatform(String[] args) {
        if (args.length < 2) {
            logger.info("&cUsage&8: platform remove &8[&aname&8]");
            return;
        }

        Platform platform = PlatformVersions.getPlatformByName(args[1]);
        if (platform == null) {
            logger.info("&cThis platform does not exist.");
            return;
        }

        File platformFile = platformsFolder.resolve(platform.getFullName() + ".jar").toFile();

        if (!platformFile.exists()) {
            logger.info("&cPlatform &a" + platform.getFullName() + " &cis not installed.");
            return;
        }

        if (platformFile.delete()) {
            logger.info("&7Platform &a" + platform.getFullName() + " &7was &cremoved.");
        } else {
            logger.info("&cFailed to remove platform " + platform.getFullName());
        }
    }

    @SneakyThrows
    private void listDownloaded() {
        if (!Files.exists(platformsFolder) || FileUtils.isEmptyDirectory(platformsFolder.toFile())) {
            logger.info("&cNo platforms are downloaded");
            return;
        }

        Files.list(platformsFolder)
                .filter(path -> path.toString().endsWith(".jar"))
                .forEach(path -> {
                    final String fileName = path.getFileName().toString().replace(".jar", "");
                    final Platform platform = PlatformVersions.getPlatformByName(fileName);
                    if (platform != null) {
                        logger.info("&8» &a" + platform.getFullName() + " &7- Proxy: &a" + platform.isProxy());
                    }
                });
    }

    private void sendHelp() {
        logger.info("&7platform list &8- &7List all available platforms");
        logger.info("&7platform list-downloaded &8- &7List downloaded platforms");
        logger.info("&7platform download &8[&aname&8] &7- Download a platform");
        logger.info("&7platform remove &8[&aname&8] &7- Remove a platform");
    }

    @Override
    public String getName() {
        return "platform";
    }

    @Override
    public String getDescription() {
        return "Manage your platforms";
    }

    @Override
    public List<String> getAliases() {
        return List.of("platforms");
    }

    @Override
    public List<String> complete(String[] args) {
        return List.of();
    }
}
