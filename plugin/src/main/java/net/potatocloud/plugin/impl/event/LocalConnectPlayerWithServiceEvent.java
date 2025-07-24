package net.potatocloud.plugin.impl.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.event.Event;

@Data
@AllArgsConstructor
public class LocalConnectPlayerWithServiceEvent implements Event {

    private String playerUsername;
    private String serviceName;

}
