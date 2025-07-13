package net.potatocloud.node.group;

import net.potatocloud.api.group.ServiceGroup;
import net.potatocloud.core.networking.NetworkServer;
import net.potatocloud.core.networking.PacketTypes;
import net.potatocloud.core.networking.packets.group.AddGroupPacket;
import net.potatocloud.node.Node;

public class ServiceGroupPacketListeners {

    public ServiceGroupPacketListeners() {
        final Node node = Node.getInstance();
        final NetworkServer server = node.getServer();

        server.registerPacketListener(PacketTypes.REQUEST_GROUPS, (session, packet) -> {
            for (ServiceGroup group : node.getGroupManager().getAllServiceGroups()) {
                session.send(new AddGroupPacket(
                        group.getName(),
                        group.getMinOnlineCount(),
                        group.getMaxOnlineCount(),
                        group.getMaxPlayers(),
                        group.getMaxMemory(),
                        group.isFallback(),
                        group.isStatic(),
                        group.getPlatformName(),
                        group.getServiceTemplates()
                ));
            }
        });


    }
}
