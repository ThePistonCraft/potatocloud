package net.potatocloud.api.platform.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.core.utils.RequestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class PaperMCPlatformVersion implements Platform {

    private final String platformName;
    private final String version;
    private String downloadUrl = "";
    private String fileHash;
    private final List<String> recommendedFlags = new ArrayList<>();

    @Override
    public String getDownloadUrl() {
        if (downloadUrl.isEmpty()) {
            getLatestVersion();
        }
        return downloadUrl;
    }

    @Override
    public boolean isProxy() {
        return getPlatformName().toLowerCase().contains("velocity");
    }

    @Override
    public String getFileHash() {
        if (fileHash == null || fileHash.isEmpty()) {
            getLatestVersion();
        }
        return fileHash;
    }

    @Override
    public List<String> getRecommendedFlags() {
        if (recommendedFlags.isEmpty()) {
            getLatestVersion();
        }
        return recommendedFlags;
    }

    @SneakyThrows
    private void getLatestVersion() {
        if (getVersion().equalsIgnoreCase("latest")) {
            final JsonObject projectJson = RequestUtil.request("https://fill.papermc.io/v3/projects/%s".formatted(getPlatformName()));
            final JsonObject versionsObject = projectJson.getAsJsonObject("versions");

            final List<String> minecraftVersions = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : versionsObject.entrySet()) {
                JsonArray versionList = entry.getValue().getAsJsonArray();
                minecraftVersions.add(versionList.get(0).getAsString());
            }

            final String latestMinecraftVersionName = minecraftVersions.get(0);
            getLatestVersion(latestMinecraftVersionName);

        } else {
            getLatestVersion(getVersion());
        }
    }

    @SneakyThrows
    private void getLatestVersion(String version) {
        final JsonObject versionsJson = RequestUtil.request("https://fill.papermc.io/v3/projects/%s/versions/%s".formatted(getPlatformName(), version));
        final int latestBuild = versionsJson.getAsJsonArray("builds").get(0).getAsInt();

        final JsonArray recommendedFlagsArray = versionsJson.getAsJsonObject("version")
                .getAsJsonObject("java")
                .getAsJsonObject("flags")
                .getAsJsonArray("recommended");

        for (JsonElement element : recommendedFlagsArray) {
            recommendedFlags.add(element.getAsString());
        }

        final JsonObject buildJson = RequestUtil.request(
                "https://fill.papermc.io/v3/projects/%s/versions/%s/builds/%d".formatted(getPlatformName(), version, latestBuild)
        );

        final JsonObject serverDefault = buildJson.getAsJsonObject("downloads").getAsJsonObject("server:default");

        final String sha256 = serverDefault.getAsJsonObject("checksums").get("sha256").getAsString();

        downloadUrl = serverDefault.get("url").getAsString();
        fileHash = sha256;
    }
}
