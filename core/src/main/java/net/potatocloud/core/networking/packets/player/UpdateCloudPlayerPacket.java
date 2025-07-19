package net.potatocloud.core.networking.packets.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UpdateCloudPlayerPacket implements Packet {

    private UUID playerUniqueId;
    private String connectedProxyName;
    private String connectedServiceName;
    private Set<PropertyData> properties;

    @Override
    public String getType() {
        return PacketTypes.UPDATE_PLAYER;
    }
}
