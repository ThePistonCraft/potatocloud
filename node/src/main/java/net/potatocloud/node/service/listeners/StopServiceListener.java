package net.potatocloud.node.service.listeners;

import lombok.AllArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.StopServicePacket;

@AllArgsConstructor
public class StopServiceListener implements PacketListener<StopServicePacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, StopServicePacket packet) {
        final Service service = serviceManager.getService(packet.getServiceName());
        if (service == null) {
            return;
        }
        service.shutdown();
    }
}
