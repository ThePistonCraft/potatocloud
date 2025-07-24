package net.potatocloud.plugin.paper;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.player.CloudPlayerManager;
import net.potatocloud.api.player.impl.CloudPlayerImpl;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.player.AddCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.RemoveCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.UpdateCloudPlayerPacket;
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

        api.getClient().registerPacketListener(PacketTypes.PLAYER_ADD, (NetworkConnection connection, AddCloudPlayerPacket packet) -> {
            playerManager.getOnlinePlayers().add(new CloudPlayerImpl(packet.getUsername(), packet.getUniqueId(), packet.getConnectedProxyName()));
        });

        api.getClient().registerPacketListener(PacketTypes.PLAYER_REMOVE, (NetworkConnection connection, RemoveCloudPlayerPacket packet) -> {
            playerManager.getOnlinePlayers().remove(playerManager.getCloudPlayer(packet.getPlayerUniqueId()));
        });

        api.getClient().registerPacketListener(PacketTypes.UPDATE_PLAYER, (NetworkConnection connection, UpdateCloudPlayerPacket packet) -> {
            final CloudPlayerImpl player = (CloudPlayerImpl) playerManager.getCloudPlayer(packet.getPlayerUniqueId());

            player.setConnectedProxyName(packet.getConnectedProxyName());
            player.setConnectedServiceName(packet.getConnectedServiceName());

            player.getProperties().clear();
            for (PropertyData data : packet.getProperties()) {
                player.setProperty(Property.fromData(data));
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
