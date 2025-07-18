package net.potatocloud.api.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PropertyData {

    private String name;
    private Object defaultValue;

    @Setter
    private Object value;

}
