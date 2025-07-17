package net.potatocloud.plugin.impl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.event.Event;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LocalConnectPlayerWithServiceEvent implements Event {

    private UUID playerUniqueId;
    private String serviceName;

}
