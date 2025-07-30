package net.potatocloud.core.networking;

import net.potatocloud.core.networking.packets.EventPacket;
import net.potatocloud.core.networking.packets.group.*;
import net.potatocloud.core.networking.packets.player.CloudPlayerAddPacket;
import net.potatocloud.core.networking.packets.player.CloudPlayerConnectPacket;
import net.potatocloud.core.networking.packets.player.CloudPlayerRemovePacket;
import net.potatocloud.core.networking.packets.player.CloudPlayerUpdatePacket;
import net.potatocloud.core.networking.packets.service.*;

public class PacketRegistry {

    public static void registerPackets(PacketManager manager) {
        manager.register(PacketIds.SERVICE_ADD, ServiceAddPacket::new);
        manager.register(PacketIds.SERVICE_REMOVE, ServiceRemovePacket::new);
        manager.register(PacketIds.SERVICE_UPDATE, ServiceUpdatePacket::new);
        manager.register(PacketIds.SERVICE_STARTED, ServiceStartedPacket::new);
        manager.register(PacketIds.REQUEST_SERVICES, RequestServicesPacket::new);
        manager.register(PacketIds.START_SERVICE, StartServicePacket::new);
        manager.register(PacketIds.STOP_SERVICE, StopServicePacket::new);
        manager.register(PacketIds.SERVICE_EXECUTE_COMMAND, ServiceExecuteCommandPacket::new);
        manager.register(PacketIds.SERVICE_COPY, ServiceCopyPacket::new);

        manager.register(PacketIds.REQUEST_GROUPS, RequestGroupsPacket::new);
        manager.register(PacketIds.GROUP_ADD, GroupAddPacket::new);
        manager.register(PacketIds.GROUP_UPDATE, GroupUpdatePacket::new);
        manager.register(PacketIds.GROUP_CREATE, GroupCreatePacket::new);
        manager.register(PacketIds.GROUP_DELETE, GroupDeletePacket::new);

        manager.register(PacketIds.PLAYER_ADD, CloudPlayerAddPacket::new);
        manager.register(PacketIds.PLAYER_REMOVE, CloudPlayerRemovePacket::new);
        manager.register(PacketIds.PLAYER_UPDATE, CloudPlayerUpdatePacket::new);
        manager.register(PacketIds.PLAYER_CONNECT, CloudPlayerConnectPacket::new);

        manager.register(PacketIds.EVENT, EventPacket::new);
    }
}
