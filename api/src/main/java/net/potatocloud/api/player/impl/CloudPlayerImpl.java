package net.potatocloud.api.player.impl;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.player.CloudPlayer;

import java.util.UUID;

@Getter
@Setter
public class CloudPlayerImpl implements CloudPlayer {

    private final String username;
    private final UUID uniqueId;
    private String connectedProxyName;
    private String connectedServiceName;

    public CloudPlayerImpl(String username, UUID uniqueId, String connectedProxyName) {
        this.username = username;
        this.uniqueId = uniqueId;
        this.connectedProxyName = connectedProxyName;
    }
}
