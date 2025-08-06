package net.potatocloud.node.platform;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.impl.PaperMCPlatformVersion;
import net.potatocloud.api.platform.impl.PurpurPlatformVersion;
import net.potatocloud.node.Node;
import net.potatocloud.node.console.Logger;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class PlatformManager {

    private final Path platformsFolder;
    private final Logger logger;

    @SneakyThrows
    public void downloadPlatform(Platform platform) {
        if (platform == null) {
            logger.info("&cThis platform does not exist.");
            return;
        }

        if (!Files.exists(platformsFolder)) {
            Files.createDirectories(platformsFolder);
        }

        final File platformFile = platformsFolder
                .resolve(platform.getFullName())
                .resolve(platform.getFullName() + ".jar")
                .toFile();

        if (!platformFile.exists()) {
            downloadPlatform(platform, platformFile);
            return;
        }

        final boolean autoUpdate = Node.getInstance().getConfig().isPlatformAutoUpdate();
        if (autoUpdate && needsUpdate(platformFile, platform)) {
            logger.info("Platform &a" + platform.getFullName() + " &7is outdated! Downloading update&8...");
            downloadPlatform(platform, platformFile);
        }
    }

    @SneakyThrows
    private void downloadPlatform(Platform platform, File platformFile) {
        logger.info("&7Downloading platform: &a" + platform.getFullName());
        URL url = URI.create(platform.getDownloadUrl()).toURL();
        FileUtils.copyURLToFile(url, platformFile, 5000, 5000);
        logger.info("&7Finished downloading platform: &a" + platform.getFullName());
    }


    @SneakyThrows
    private boolean needsUpdate(File currentPlatformFile, Platform platform) {
        final String platformHash = platform.getFileHash();
        if (platformHash == null || platformHash.isEmpty()) {
            return false;
        }

        try (FileInputStream stream = new FileInputStream(currentPlatformFile)) {
            final String currentFileHash = switch (platform) {
                case PaperMCPlatformVersion ignored -> DigestUtils.sha256Hex(stream);
                case PurpurPlatformVersion ignored -> DigestUtils.md5Hex(stream);
                default -> DigestUtils.sha256Hex(stream);
            };

            return !currentFileHash.equals(platformHash);
        }
    }
}