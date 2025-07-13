package net.potatocloud.api.platform;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.potatocloud.api.platform.impl.PandaSpigotVersion;
import net.potatocloud.api.platform.impl.PaperMCPlatformVersion;
import net.potatocloud.api.platform.impl.PurpurPlatformVersion;

public enum PlatformVersions {

    // PAPER
    PAPER_LATEST(new PaperMCPlatformVersion("paper", "latest")),
    PAPER_1_21_7(new PaperMCPlatformVersion("paper", "1.21.7")),
    PAPER_1_21_6(new PaperMCPlatformVersion("paper", "1.21.6")),
    PAPER_1_21_5(new PaperMCPlatformVersion("paper", "1.21.5")),
    PAPER_1_21_4(new PaperMCPlatformVersion("paper", "1.21.4")),
    PAPER_1_21_3(new PaperMCPlatformVersion("paper", "1.21.3")),
    PAPER_1_20_6(new PaperMCPlatformVersion("paper", "1.20.6")),
    PAPER_1_20_5(new PaperMCPlatformVersion("paper", "1.20.5")),
    PAPER_1_20_4(new PaperMCPlatformVersion("paper", "1.20.4")),

    // PURPUR
    PURPUR_LATEST(new PurpurPlatformVersion("latest")),
    PURPUR_1_21_7(new PurpurPlatformVersion("1.21.7")),
    PURPUR_1_21_6(new PurpurPlatformVersion("1.21.6")),
    PURPUR_1_21_5(new PurpurPlatformVersion("1.21.5")),
    PURPUR_1_21_4(new PurpurPlatformVersion("1.21.4")),
    PURPUR_1_21_3(new PurpurPlatformVersion("1.21.3")),
    PURPUR_1_20_6(new PurpurPlatformVersion("1.20.6")),
    PURPUR_1_20_5(new PurpurPlatformVersion("1.20.5")),
    PURPUR_1_20_4(new PurpurPlatformVersion("1.20.4")),

    // PANDASPIGOT
    PANDASPIGOT_LATEST(new PandaSpigotVersion()),

    // VELOCITY
    VELOCITY_LATEST(new PaperMCPlatformVersion("velocity", "latest")),
    VELOCITY_3_40(new PaperMCPlatformVersion("velocity", "3.3.0-SNAPSHOT")),
    VELOCITY_3_30(new PaperMCPlatformVersion("velocity", "3.4.0-SNAPSHOT"));

    @Getter
    @Accessors(fluent = true)
    private final Platform platform;

    PlatformVersions(Platform platform) {
        this.platform = platform;
    }

    public static Platform getPlatformByName(String name) {
        for (PlatformVersions platform : values()) {
            if (platform.platform().getFullName().equalsIgnoreCase(name.toLowerCase())) {
                return platform.platform();
            }
        }
        return null;
    }

    public static boolean exists(String name) {
        return getPlatformByName(name) != null;
    }
}
