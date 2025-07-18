package net.potatocloud.plugin.paper;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.packets.service.ServiceStartedPacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlugin extends JavaPlugin implements Listener {

    private PluginCloudAPI api;
    private Service thisService;

    @Override
    public void onLoad() {
        api = new PluginCloudAPI();
    }

    @Override
    public void onEnable() {
        initThisService();
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void initThisService() {
        thisService = CloudAPI.getInstance().getThisService();
        // service manager is still null or the services have not finished loading
        if (thisService == null) {
            // retry after 1 second
            getServer().getScheduler().runTaskLater(this, this::initThisService, 20L);
            return;
        }

        api.getClient().send(new ServiceStartedPacket(thisService.getName()));
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (thisService == null) {
            return;
        }
        event.setMaxPlayers(thisService.getMaxPlayers());
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (thisService == null) {
            return;
        }
        if (getServer().getOnlinePlayers().size() < thisService.getMaxPlayers()) {
            return;
        }
        if (event.getPlayer().hasPermission("potatocloud.maxplayers.bypass")) {
            return;
        }
        event.disallow(PlayerLoginEvent.Result.KICK_FULL, MiniMessage.miniMessage().deserialize("<red>The server has reached its maximum players!"));
    }

    @Override
    public void onDisable() {
        api.shutdown();
    }
}
