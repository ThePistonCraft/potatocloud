package net.potatocloud.node.listeners.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.UpdateServicePacket;

@RequiredArgsConstructor
public class UpdateServiceListener implements PacketListener<UpdateServicePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, UpdateServicePacket packet) {
        final Service service = serviceManager.getService(packet.getServiceName());
        service.setState(ServiceState.valueOf(packet.getStateName()));
        service.setMaxPlayers(packet.getMaxPlayers());
    }
}
