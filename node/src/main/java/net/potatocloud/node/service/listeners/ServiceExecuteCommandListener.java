package net.potatocloud.node.service.listeners;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceManager;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.service.ServiceExecuteCommandPacket;

@RequiredArgsConstructor
public class ServiceExecuteCommandListener implements PacketListener<ServiceExecuteCommandPacket> {

    private final ServiceManager serviceManager;

    @Override
    public void onPacket(NetworkConnection connection, ServiceExecuteCommandPacket packet) {
        final Service service = serviceManager.getService(packet.getServiceName());
        if (service == null) {
            return;
        }
        service.executeCommand(packet.getCommand());
    }
}
