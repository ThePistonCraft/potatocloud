package net.potatocloud.core.networking;

import net.potatocloud.core.networking.packets.EventPacket;
import net.potatocloud.core.networking.packets.MessagePacket;
import net.potatocloud.core.networking.packets.group.AddGroupPacket;
import net.potatocloud.core.networking.packets.group.RequestGroupsPacket;
import net.potatocloud.core.networking.packets.group.UpdateGroupPacket;
import net.potatocloud.core.networking.packets.player.AddCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.ConnectCloudPlayerWithServicePacket;
import net.potatocloud.core.networking.packets.player.RemoveCloudPlayerPacket;
import net.potatocloud.core.networking.packets.player.UpdateCloudPlayerPacket;
import net.potatocloud.core.networking.packets.service.*;

public class PacketRegistry {

    public static void registerPackets(PacketManager manager) {
        manager.register(PacketTypes.MESSAGE, MessagePacket.class);

        manager.register(PacketTypes.SERVICE_ADD, ServiceAddPacket.class);
        manager.register(PacketTypes.SERVICE_REMOVE, ServiceRemovePacket.class);
        manager.register(PacketTypes.UPDATE_SERVICE, UpdateServicePacket.class);
        manager.register(PacketTypes.SERVICE_STARTED, ServiceStartedPacket.class);
        manager.register(PacketTypes.REQUEST_SERVICES, RequestServicesPacket.class);
        manager.register(PacketTypes.START_SERVICE, StartServicePacket.class);
        manager.register(PacketTypes.SHUTDOWN_SERVICE, ShutdownServicePacket.class);
        manager.register(PacketTypes.SERVICE_EXECUTE_COMMAND, ServiceExecuteCommandPacket.class);

        manager.register(PacketTypes.REQUEST_GROUPS, RequestGroupsPacket.class);
        manager.register(PacketTypes.GROUP_ADD, AddGroupPacket.class);
        manager.register(PacketTypes.UPDATE_GROUP, UpdateGroupPacket.class);

        manager.register(PacketTypes.EVENT, EventPacket.class);

        manager.register(PacketTypes.PLAYER_ADD, AddCloudPlayerPacket.class);
        manager.register(PacketTypes.PLAYER_REMOVE, RemoveCloudPlayerPacket.class);
        manager.register(PacketTypes.UPDATE_PLAYER, UpdateCloudPlayerPacket.class);
        manager.register(PacketTypes.CONNECT_PLAYER, ConnectCloudPlayerWithServicePacket.class);
    }
}
