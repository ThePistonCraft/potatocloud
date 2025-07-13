package net.potatocloud.api.platform.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.potatocloud.api.platform.Platform;
import net.potatocloud.api.utils.RequestUtil;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PurpurPlatformVersion implements Platform {

    private final String version;
    private String downloadUrl = "";
    private String fileHash;

    @Override
    public String getPlatformName() {
        return "purpur";
    }

    @Override
    public String getDownloadUrl() {
        if (downloadUrl.isEmpty()) {
            getLatestVersion();
        }
        return downloadUrl;
    }

    @Override
    public boolean isProxy() {
        return false;
    }

    public String getFileHash() {
        if (fileHash == null || fileHash.isEmpty()) {
            getLatestVersion();
        }
        return fileHash;
    }

    private void getLatestVersion() {
        if (getVersion().equalsIgnoreCase("latest")) {
            final JsonArray versionsArray = RequestUtil.request("https://api.purpurmc.org/v2/purpur/").get("versions").getAsJsonArray();
            final String latestMinecraftVersion = versionsArray.get(versionsArray.size() - 1).getAsString();

            getLatestVersion(latestMinecraftVersion);
        } else {
            getLatestVersion(getVersion());
        }
    }

    private void getLatestVersion(String version) {
        final String latestBuild = RequestUtil.request("https://api.purpurmc.org/v2/purpur/" + version)
                .get("builds")
                .getAsJsonObject()
                .get("latest")
                .getAsString();

        final JsonObject buildJson = RequestUtil.request("https://api.purpurmc.org/v2/purpur/" + version + "/" + latestBuild);

        fileHash = buildJson.get("md5").getAsString();
        downloadUrl = "https://api.purpurmc.org/v2/purpur/" + version + "/" + latestBuild + "/download";
    }

    @Override
    public List<String> getRecommendedFlags() {
        return List.of();
    }
}
