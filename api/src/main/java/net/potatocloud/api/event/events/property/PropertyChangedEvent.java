package net.potatocloud.api.event.events.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.potatocloud.api.event.Event;
import net.potatocloud.api.property.Property;

@Data
@AllArgsConstructor
public class PropertyChangedEvent implements Event {

    private final String holderName;
    private final Property property;
    private final Object oldValue;
    private final Object newValue;

}
