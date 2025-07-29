package net.potatocloud.node.listeners.service;

import lombok.AllArgsConstructor;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.StopServicePacket;

@AllArgsConstructor
public class ShutdownServiceListener implements PacketListener<StopServicePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, StopServicePacket packet) {
        serviceManager.getService(packet.getServiceName()).shutdown();
    }
}
