package net.potatocloud.api.property;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
public class Property {

    public static final Property GAME_STATE = Property.ofString("gameState", "INGAME");

    private final String name;
    private final Object defaultValue;

    @Setter
    private Object value;

    private Property(String name, Object defaultValue, Object value) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = value;
    }

    public static Property ofString(String name, String defaultValue) {
        return new Property(name, defaultValue, defaultValue);
    }

    public static Property ofInt(String name, int defaultValue) {
        return new Property(name, defaultValue, defaultValue);
    }

    public static Property ofFloat(String name, float defaultValue) {
        return new Property(name, defaultValue, defaultValue);
    }

    public static Property ofBoolean(String name, boolean defaultValue) {
        return new Property(name, defaultValue, defaultValue);
    }

    public static Property of(String name, Object defaultValue, Object value) {
        return new Property(name, defaultValue, value);
    }

    public static Set<Property> getDefaultProperties() {
        return Set.of(GAME_STATE);
    }
}
