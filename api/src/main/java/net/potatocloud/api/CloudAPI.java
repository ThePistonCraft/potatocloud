package net.potatocloud.api;

import lombok.Getter;
import net.potatocloud.api.event.EventManager;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.service.ServiceManager;

@Getter
public abstract class CloudAPI {

    @Getter
    private static CloudAPI instance;

    public CloudAPI() {
        instance = this;
    }

    public abstract ServiceGroupManager getServiceGroupManager();

    public abstract ServiceManager getServiceManager();

    public abstract EventManager getEventManager();

    public abstract CloudPlayerManager getPlayerManager();

}
