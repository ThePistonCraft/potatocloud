package net.potatocloud.plugin.paper;

import net.potatocloud.api.service.Service;
import net.potatocloud.core.networking.packets.service.ServiceStartedPacket;
import net.potatocloud.plugin.impl.PluginCloudAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperPlugin extends JavaPlugin {

    private PluginCloudAPI api;
    private Service thisService;

    @Override
    public void onEnable() {
        api = new PluginCloudAPI();
        thisService = api.getServiceManager().getService(System.getProperty("potatocloud.service.name"));
        api.getClient().send(new ServiceStartedPacket(thisService.getName()));

        Bukkit.setMaxPlayers(thisService.getMaxPlayers());
    }

    @Override
    public void onDisable() {
        api.shutdown();
    }
}
