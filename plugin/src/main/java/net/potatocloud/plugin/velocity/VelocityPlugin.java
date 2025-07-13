package net.potatocloud.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.potatocloud.api.event.events.ServiceStartedEvent;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.core.networking.packets.service.ServiceStartedPacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;

import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.logging.Logger;

public class VelocityPlugin {

    private final PluginCloudAPI api;
    private final Service thisService;

    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        api = new PluginCloudAPI();
        thisService = api.getServiceManager().getService(System.getProperty("potatocloud.service.name"));

        api.getClient().send(new ServiceStartedPacket(thisService.getName()));


        for (Service service : api.getServiceManager().getAllOnlineServices()) {
            registerServer(service);
        }

        api.getEventManager().on(ServiceStartedEvent.class, event -> {
            final Service service = api.getServiceManager().getService(event.getServiceName());
            registerServer(service);
        });
    }

    private void registerServer(Service service) {
        if (service.getServiceGroup().getPlatform().isProxy()) {
            return;
        }

        server.registerServer(new ServerInfo(service.getName(), new InetSocketAddress("0.0.0.0", service.getPort())));
    }

    @Subscribe
    public void handle(PlayerChooseInitialServerEvent event) {
        final Service bestFallback = api.getServiceManager().getAllOnlineServices().stream()
                .filter(s -> s.getServiceGroup().isFallback())
                .filter(s -> s.getState() == ServiceState.RUNNING)
                .min(Comparator.comparingInt(Service::getOnlinePlayers))
                .orElse(null);

        event.setInitialServer(server.getServer(bestFallback.getName()).get());
    }


    @Subscribe
    public void handle(ProxyPingEvent event) {
        event.setPing(event.getPing().asBuilder()
                .onlinePlayers(server.getPlayerCount())
                .maximumPlayers(thisService.getMaxPlayers())
                .build());
    }

    @Subscribe
    public void handle(LoginEvent event) {
        final Player player = event.getPlayer();

        if (server.getPlayerCount() < thisService.getMaxPlayers()) {
            return;
        }

        if (player.hasPermission("potatocloud.maxplayers.bypass")) {
            return;
        }

        player.disconnect(MiniMessage.miniMessage().deserialize("<red>The server has reached its maximum players!"));
    }
}
