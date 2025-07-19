package net.potatocloud.node.listeners.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.ServiceCopyPacket;

@RequiredArgsConstructor
public class ServiceCopyListener implements PacketListener<ServiceCopyPacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, ServiceCopyPacket packet) {
        final Service service = serviceManager.getService(packet.getServiceName());
        service.copy(packet.getTemplateName(), packet.getFilter());
    }
}
