package net.potatocloud.plugin.impl.listener.group;

import lombok.RequiredArgsConstructor;
import net.potatocloud.api.group.impl.ServiceGroupImpl;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.PacketListener;
import net.potatocloud.core.networking.packets.group.AddGroupPacket;
import net.potatocloud.plugin.impl.group.ServiceGroupManagerImpl;

@RequiredArgsConstructor
public class AddGroupListener implements PacketListener<AddGroupPacket> {

    private final ServiceGroupManagerImpl groupManager;

    @Override
    public void onPacket(NetworkConnection connection, AddGroupPacket packet) {
        groupManager.addServiceGroup(new ServiceGroupImpl(
                packet.getName(),
                packet.getPlatformName(),
                packet.getServiceTemplates(),
                packet.getMinOnlineCount(),
                packet.getMaxOnlineCount(),
                packet.getMaxPlayers(),
                packet.getMaxMemory(),
                packet.isFallback(),
                packet.isStatic()
        ));
    }
}
