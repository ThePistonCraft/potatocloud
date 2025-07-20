package net.potatocloud.plugins.notify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.event.events.service.PreparedServiceStartingEvent;
import net.potatocloud.api.event.events.service.ServiceStartedEvent;
import net.potatocloud.api.event.events.service.ServiceStoppedEvent;
import net.potatocloud.api.service.Service;

import java.util.logging.Logger;

@Slf4j
public class NotifyPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final MessagesConfig messagesConfig;
    private final Config config;

    @Inject
    public NotifyPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.messagesConfig = new MessagesConfig();
        this.messagesConfig.load();
        this.config = new Config();
        this.config.load();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        this.handleIncomingEvents();
    }

    private void handleIncomingEvents() {
        final CloudAPI cloudAPI = CloudAPI.getInstance();

        // starting Server
        cloudAPI.getEventManager().on(PreparedServiceStartingEvent.class, startingEvent -> {
            final Service service = cloudAPI.getServiceManager().getService(startingEvent.getServiceName());
            // send message;
            this.server.getAllPlayers()
                    .stream()
                    .filter(player -> player.hasPermission(this.config.getPermission()))
                    .forEach(player -> {
                        player.sendMessage(this.messagesConfig.get("starting")
                                .clickEvent(ClickEvent.runCommand("/server " + startingEvent.getServiceName()))
                                .hoverEvent(HoverEvent.showText(this.messagesConfig.get("hover")
                                        .replaceText(text -> text.match("%service%").replacement(service.getName()))))
                                .replaceText(text -> text.match("%service%").replacement(service.getName()))
                                .replaceText(text -> text.match("%port%").replacement(service.getPort() + ""))
                                .replaceText(text -> text.match("%group%").replacement(service.getServiceGroup().getName() + ""))
                        );
                    });
        });

        // start Server
        cloudAPI.getEventManager().on(ServiceStartedEvent.class, startedEvent -> {
            final Service service = cloudAPI.getServiceManager().getService(startedEvent.getServiceName());
            // send message;
            this.server.getAllPlayers()
                    .stream()
                    .filter(player -> player.hasPermission(this.config.getPermission()))
                    .forEach(player -> {
                        player.sendMessage(this.messagesConfig.get("start")
                                .clickEvent(ClickEvent.runCommand("/server " + startedEvent.getServiceName()))
                                .hoverEvent(HoverEvent.showText(this.messagesConfig.get("hover")
                                        .replaceText(text -> text.match("%service%").replacement(service.getName()))))
                                .replaceText(text -> text.match("%service%").replacement(service.getName()))
                                .replaceText(text -> text.match("%port%").replacement(service.getPort() + ""))
                                .replaceText(text -> text.match("%group%").replacement(service.getServiceGroup().getName() + ""))
                        );
                    });
        });

        // stop server
        cloudAPI.getEventManager().on(ServiceStoppedEvent.class, stoppedEvent -> {
            // send message;
            this.server.getAllPlayers()
                    .stream()
                    .filter(player -> player.hasPermission(this.config.getPermission()))
                    .forEach(player -> {
                        player.sendMessage(this.messagesConfig.get("stop")
                                .replaceText(text -> text.match("%service%").replacement(stoppedEvent.getServiceName())));
                    });
        });
    }
}
