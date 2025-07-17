package net.potatocloud.node.listeners.service;

import lombok.AllArgsConstructor;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.ShutdownServicePacket;

@AllArgsConstructor
public class ShutdownServiceListener implements PacketListener<ShutdownServicePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, ShutdownServicePacket packet) {
        serviceManager.getService(packet.getServiceName()).shutdown();
    }
}
