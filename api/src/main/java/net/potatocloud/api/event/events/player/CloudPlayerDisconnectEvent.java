package net.potatocloud.api.event.events.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.event.Event;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CloudPlayerDisconnectEvent implements Event {

    private final UUID playerUniqueId;
    private final String playerName;

}
