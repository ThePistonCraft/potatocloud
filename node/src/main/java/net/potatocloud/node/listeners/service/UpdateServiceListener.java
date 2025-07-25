package net.potatocloud.node.listeners.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.api.service.ServiceStatus;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.UpdateServicePacket;
import net.potatocloud.node.Node;

@RequiredArgsConstructor
public class UpdateServiceListener implements PacketListener<UpdateServicePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, UpdateServicePacket packet) {
        final Service service = serviceManager.getService(packet.getServiceName());
        service.setStatus(ServiceStatus.valueOf(packet.getStatusName()));
        service.setMaxPlayers(packet.getMaxPlayers());
        service.getProperties().clear();
        for (PropertyData data : packet.getProperties()) {
            service.setProperty(Property.fromData(data));
        }

        Node.getInstance().getServer().getConnectedSessions().stream()
                .filter(networkConnection -> !networkConnection.equals(connection))
                .forEach(networkConnection -> networkConnection.send(packet));
    }
}
