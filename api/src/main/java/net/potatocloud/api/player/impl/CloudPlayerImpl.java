package net.potatocloud.api.player.impl;

import lombok.Getter;
import lombok.Setter;
import net.potatocloud.api.player.CloudPlayer;
import net.potatocloud.api.property.Property;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CloudPlayerImpl implements CloudPlayer {

    private final String username;
    private final UUID uniqueId;
    private String connectedProxyName;
    private String connectedServiceName;
    private final Set<Property> properties;

    public CloudPlayerImpl(String username, UUID uniqueId, String connectedProxyName) {
        this.username = username;
        this.uniqueId = uniqueId;
        this.connectedProxyName = connectedProxyName;
        this.properties = new HashSet<>();
    }

    @Override
    public String getPropertyHolderName() {
        return getUsername();
    }
}
