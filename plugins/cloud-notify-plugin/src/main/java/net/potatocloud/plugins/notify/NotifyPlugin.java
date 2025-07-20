package net.potatocloud.plugins.notify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
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
    private final CloudAPI cloudAPI;

    private final Logger logger;
    private final MessagesConfig messagesConfig;
    private final Config config;

    @Inject
    public NotifyPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        this.cloudAPI = CloudAPI.getInstance();
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
        // service is starting
        this.cloudAPI.getEventManager().on(PreparedServiceStartingEvent.class, event ->
                this.sendMessageToAuthorizedPlayers("starting", this.cloudAPI.getServiceManager().getService(event.getServiceName()), event.getServiceName()));

        // service is started
        this.cloudAPI.getEventManager().on(ServiceStartedEvent.class, event ->
                this.sendMessageToAuthorizedPlayers("start", this.cloudAPI.getServiceManager().getService(event.getServiceName()), event.getServiceName()));

        // service is stopped
        this.cloudAPI.getEventManager().on(ServiceStoppedEvent.class, event ->
                this.sendSimpleMessageToAuthorizedPlayers("stop", event.getServiceName()));
    }

    private void sendMessageToAuthorizedPlayers(String messageKey, Service service, String commandServiceName) {
        final Component baseMessage = this.messagesConfig.get(messageKey).clickEvent(ClickEvent.runCommand("/server " + commandServiceName))
                .hoverEvent(HoverEvent.showText(this.messagesConfig.get("hover").replaceText(builder -> builder.match("%service%").replacement(service.getName()))))
                .replaceText(builder -> builder.match("%service%").replacement(service.getName()))
                .replaceText(builder -> builder.match("%port%").replacement(String.valueOf(service.getPort())))
                .replaceText(builder -> builder.match("%group%").replacement(service.getServiceGroup().getName()));

        this.server.getAllPlayers().stream().filter(player -> player.hasPermission(this.config.getPermission()))
                .forEach(player -> player.sendMessage(baseMessage));
    }

    private void sendSimpleMessageToAuthorizedPlayers(String messageKey, String serviceName) {
        final Component message = this.messagesConfig.get(messageKey)
                .replaceText(builder -> builder.match("%service%").replacement(serviceName));

        this.server.getAllPlayers().stream().filter(player -> player.hasPermission(this.config.getPermission()))
                .forEach(player -> player.sendMessage(message));
    }
}
