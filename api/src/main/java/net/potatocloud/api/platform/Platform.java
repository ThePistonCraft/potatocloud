package net.potatocloud.api.platform;

import java.util.List;

public interface Platform {

    String getPlatformName();

    String getVersion();

    String getDownloadUrl();

    default String getFullName() {
        return getPlatformName() + "-" + getVersion();
    }

    boolean isProxy();

    String getFileHash();

    List<String> getRecommendedFlags();

}
