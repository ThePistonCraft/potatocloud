package net.potatocloud.api.property;

import net.potatocloud.api.CloudAPI;
import net.potatocloud.api.event.events.property.PropertyChangedEvent;

import java.util.Set;

public interface PropertyHolder {

    Set<Property> getProperties();

    default Property getProperty(String name) {
        return getProperties().stream()
                .filter(property -> property.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    default void setProperty(Property property, Object value) {
        Object oldValue = null;

        final Property existingProperty = getProperty(property.getName());
        if (existingProperty != null) {
            oldValue = existingProperty.getValue();
            existingProperty.setValue(value);
        } else {
            property.setValue(value);
            getProperties().add(property);
        }

        CloudAPI.getInstance().getEventManager().call(new PropertyChangedEvent(property.getData(), oldValue, value));
    }


    default void setProperty(Property property) {
        setProperty(property, property.getValue());
    }

    default boolean hasProperty(Property property) {
        return getProperties().contains(property);
    }
}
