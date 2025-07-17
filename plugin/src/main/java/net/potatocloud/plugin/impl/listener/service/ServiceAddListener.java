package net.potatocloud.plugin.impl.listener.service;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.ServiceAddPacket;
import net.potatocloud.plugin.impl.service.ServiceImpl;
import net.potatocloud.plugin.impl.service.ServiceManagerImpl;

@RequiredArgsConstructor
public class ServiceAddListener implements PacketListener<ServiceAddPacket> {

    private final ServiceManagerImpl serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, ServiceAddPacket packet) {
        final Service service = new ServiceImpl(
                packet.getName(),
                packet.getServiceId(),
                packet.getPort(),
                packet.getStartTimestamp(),
                CloudAPI.getInstance().getServiceGroupManager().getServiceGroup(packet.getGroupName()),
                ServiceState.valueOf(packet.getState()),
                packet.getOnlinePlayers(),
                packet.getUsedMemory());

        if (!serviceManager.getAllServices().contains(service)) {
            serviceManager.addService(service);
        }
    }
}
