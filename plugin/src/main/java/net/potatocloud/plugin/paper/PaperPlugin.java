package net.potatocloud.plugin.paper;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketIds;
import net.potatocloud.core.networking.packets.player.CloudPlayerAddPacket;
import net.potatocloud.core.networking.packets.player.CloudPlayerRemovePacket;
import net.potatocloud.core.networking.packets.player.CloudPlayerUpdatePacket;
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

        final CloudPlayerManager playerManager = api.getPlayerManager();

        api.getClient().registerPacketListener(PacketIds.PLAYER_ADD, (NetworkConnection connection, CloudPlayerAddPacket packet) -> {
            playerManager.getOnlinePlayers().add(new CloudPlayerImpl(packet.getUsername(), packet.getUniqueId(), packet.getConnectedProxyName()));
        });

        api.getClient().registerPacketListener(PacketIds.PLAYER_REMOVE, (NetworkConnection connection, CloudPlayerRemovePacket packet) -> {
            playerManager.getOnlinePlayers().remove(playerManager.getCloudPlayer(packet.getPlayerUniqueId()));
        });

        api.getClient().registerPacketListener(PacketIds.PLAYER_UPDATE, (NetworkConnection connection, CloudPlayerUpdatePacket packet) -> {
            final CloudPlayerImpl player = (CloudPlayerImpl) playerManager.getCloudPlayer(packet.getPlayerUniqueId());
            if (player == null) {
                return;
            }

            player.setConnectedProxyName(packet.getConnectedProxyName());
            player.setConnectedServiceName(packet.getConnectedServiceName());

            player.getProperties().clear();
            for (Property property : packet.getProperties()) {
                player.setProperty(property, property.getValue(), false);
            }
        });

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
