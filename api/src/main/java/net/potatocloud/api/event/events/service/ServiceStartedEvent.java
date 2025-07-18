package net.potatocloud.api.event.events.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.event.Event;

@Data
@AllArgsConstructor
public class ServiceStartedEvent implements Event {

    private String serviceName;

}
