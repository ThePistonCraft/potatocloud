package net.potatocloud.api.platform.impl;

import net.potatocloud.api.platform.Platform;

import java.util.List;

public class PandaSpigotVersion implements Platform {

    @Override
    public String getPlatformName() {
        return "PandaSpigot";
    }

    @Override
    public String getVersion() {
        return "latest";
    }

    @Override
    public String getDownloadUrl() {
        return "https://downloads.hpfxd.com/v2/projects/pandaspigot/versions/1.8.8/builds/latest/downloads/paperclip";
    }

    @Override
    public boolean isProxy() {
        return false;
    }

    @Override
    public String getFileHash() {
        // not available
        return "";
    }

    @Override
    public List<String> getRecommendedFlags() {
        return List.of();
    }
}
