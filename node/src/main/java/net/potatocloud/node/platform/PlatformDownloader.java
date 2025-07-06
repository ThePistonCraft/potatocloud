package net.potatocloud.node.platform;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.platform.impl.PaperMCPlatformVersion;
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
public class PlatformDownloader {

    private final Path platformsFolder;
    private final Logger logger;

    @SneakyThrows
    public void download(Platform platform) {
        if (platform == null) {
            logger.info("&cThis platform does not exist.");
            return;
        }

        if (!Files.exists(platformsFolder)) {
            Files.createDirectories(platformsFolder);
        }

        final File platformFile = platformsFolder.resolve(platform.getFullName() + ".jar").toFile();

        boolean needsDownload = true;
        if (platformFile.exists()) {
            needsDownload = needsUpdate(platformFile, platform);
            if (needsDownload) {
                logger.info("Platform&8: &a" + platform.getFullName() + " &7is outdated! Downloading update&8...");
            }
        }

        if (needsDownload) {
            logger.info("&7Downloading platform: &a" + platform.getFullName());
            final URL url = URI.create(platform.getDownloadUrl()).toURL();
            FileUtils.copyURLToFile(url, platformFile, 5000, 5000);
            logger.info("&7Finished downloading platform: &a" + platform.getFullName());
        }
    }

    @SneakyThrows
    private boolean needsUpdate(File currentPlatformFile, Platform platform) {
        if (platform.getFileHash() == null || platform.getFileHash().isEmpty()) {
            return false;
        }

        try (FileInputStream stream = new FileInputStream(currentPlatformFile)) {
            String currentFileHash = "";
            if (platform instanceof PaperMCPlatformVersion) {
                currentFileHash = DigestUtils.sha256Hex(stream);
            } else {
                //used by purpermc
                currentFileHash = DigestUtils.md5Hex(stream);
            }

            return !currentFileHash.equals(platform.getFileHash());
        }
    }
}