package net.potatocloud.node.service.listener;

import net.potatocloud.api.event.events.ServiceStartedEvent;
import net.potatocloud.api.service.Service;
import net.potatocloud.api.service.ServiceState;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.service.RequestServicesPacket;
import net.potatocloud.core.networking.packets.service.ServiceAddPacket;
import net.potatocloud.core.networking.packets.service.ServiceStartedPacket;
import net.potatocloud.node.Node;

public class ServicePacketListeners {

    public ServicePacketListeners() {
        Node.getInstance().getServer().registerPacketListener(PacketTypes.SERVICE_STARTED,  (NetworkConnection session, ServiceStartedPacket packet) -> {
            Node.getInstance().getLogger().info("The Service &a" + packet.getServiceName() + "&7 is now &aonline");
            Node.getInstance().getServiceManager().getService(packet.getServiceName()).setState(ServiceState.RUNNING);
            Node.getInstance().getServiceManager().getService(packet.getServiceName()).update();

            // call service started event
            Node.getInstance().getEventManager().call(new ServiceStartedEvent(packet.getServiceName()));
        });

        Node.getInstance().getServer().registerPacketListener(PacketTypes.REQUEST_SERVICES, (NetworkConnection session, RequestServicesPacket packet) -> {
            for (Service service : Node.getInstance().getServiceManager().getAllOnlineServices()) {
                session.send(new ServiceAddPacket(
                        service.getName(),
                        service.getServiceId(),
                        service.getPort(),
                        service.getStartTimestamp(),
                        service.getServiceGroup().getName(),
                        service.getState().name(),
                        service.getOnlinePlayers(),
                        service.getUsedMemory()
                ));
            }
        });
    }
}
