package net.potatocloud.node.listeners.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.ServiceGroupManager;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.StartServicePacket;

@RequiredArgsConstructor
public class StartServiceListener implements PacketListener<StartServicePacket> {

    private final ServiceManager serviceManager;
    private final ServiceGroupManager serviceGroupManager;

    @Override
    public void onPacket(NetworkConnection connection, StartServicePacket packet) {
        serviceManager.startService(serviceGroupManager.getServiceGroup(packet.getGroupName()));
    }
}
