package net.potatocloud.plugin.impl.listener.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.ServiceUpdatePacket;

@RequiredArgsConstructor
public class ServiceUpdateListener implements PacketListener<ServiceUpdatePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, ServiceUpdatePacket packet) {
        final Service service = serviceManager.getService(packet.getServiceName());
        service.setStatus(ServiceStatus.valueOf(packet.getStatus()));
        service.setMaxPlayers(packet.getMaxPlayers());
        service.getProperties().clear();
        for (Property property : packet.getProperties()) {
            service.setProperty(property);
        }
    }
}
