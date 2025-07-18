package net.potatocloud.api.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
public class Property {

    public static final Property GAME_STATE = new Property("gameState", "INGAME");
    private final PropertyData data;

    public Property(String name, Object defaultValue) {
        this.data = new PropertyData(name, defaultValue, defaultValue);
    }

    public Property(String name, Object defaultValue, Object value) {
        this.data = new PropertyData(name, defaultValue, value);
    }

    public static Property fromData(PropertyData data) {
        return new Property(data.getName(), data.getDefaultValue(), data.getValue());
    }

    public static Set<Property> getDefaultProperties() {
        return Set.of(GAME_STATE);
    }

    public String getName() {
        return data.getName();
    }

    public Object getDefaultValue() {
        return data.getDefaultValue();
    }

    public Object getValue() {
        return data.getValue();
    }

    public void setValue(Object value) {
        data.setValue(value);
    }
}
