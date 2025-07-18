package net.potatocloud.core.networking.packets.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.property.Property;
import net.potatocloud.api.property.PropertyData;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketTypes;

import java.util.Set;

@Data
@AllArgsConstructor
public class UpdateServicePacket implements Packet {

    private String serviceName;
    private String statusName;
    private int maxPlayers;
    private Set<PropertyData> properties;

    @Override
    public String getType() {
        return PacketTypes.UPDATE_SERVICE;
    }
}
