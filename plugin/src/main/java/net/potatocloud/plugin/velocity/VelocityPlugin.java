package net.potatocloud.plugin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.potatocloud.api.event.events.ServiceStartedEvent;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.core.networking.packets.service.ServiceStartedPacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;

import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class VelocityPlugin {

    private final PluginCloudAPI api;
    private final ProxyServer server;
    private final Logger logger;
    private Service thisService;

    @Inject
    public VelocityPlugin(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        api = new PluginCloudAPI();
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        initServices();

        api.getEventManager().on(ServiceStartedEvent.class, startedEvent -> {
            // service manager should be initialized by now
            final Service service = api.getServiceManager().getService(startedEvent.getServiceName());
            registerServer(service);
        });
    }

    private void initServices() {
        thisService = api.getServiceManager().getService(System.getProperty("potatocloud.service.name"));
        // service manager is still null or the services have not finished loading
        if (thisService == null) {
            // retry after 1 second
            server.getScheduler().buildTask(this, this::initServices).delay(1, TimeUnit.SECONDS).schedule();
            return;
        }

        api.getClient().send(new ServiceStartedPacket(thisService.getName()));

        for (Service service : api.getServiceManager().getAllServices()) {
            registerServer(service);
        }
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        api.shutdown();
    }

    private void registerServer(Service service) {
        if (service.getServiceGroup().getPlatform().isProxy()) {
            return;
        }
        server.registerServer(new ServerInfo(service.getName(), new InetSocketAddress("0.0.0.0", service.getPort())));
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {

        for (Service allOnlineService : api.getServiceManager().getAllServices()) {
            logger.info(allOnlineService.getName() + " " + allOnlineService.getState());
        }

        final Service bestFallbackService = api.getServiceManager().getAllServices().stream()
                .filter(service -> service.getServiceGroup().isFallback())
                .filter(service -> service.getState() == ServiceState.RUNNING)
                .min(Comparator.comparingInt(Service::getOnlinePlayers))
                .orElse(null);

        final Optional<RegisteredServer> bestFallbackServer = server.getServer(bestFallbackService.getName());
        if (bestFallbackServer.isEmpty()) {
            return;
        }
        event.setInitialServer(bestFallbackServer.get());
    }


    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        if (thisService == null) {
            return;
        }
        event.setPing(event.getPing().asBuilder()
                .onlinePlayers(server.getPlayerCount())
                .maximumPlayers(thisService.getMaxPlayers())
                .build());
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        if (thisService == null) {
            return;
        }
        if (server.getPlayerCount() < thisService.getMaxPlayers()) {
            return;
        }
        if (event.getPlayer().hasPermission("potatocloud.maxplayers.bypass")) {
            return;
        }
        event.getPlayer().disconnect(MiniMessage.miniMessage().deserialize("<red>The server has reached its maximum players!"));
    }
}
