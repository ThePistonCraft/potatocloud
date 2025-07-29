package net.potatocloud.core.networking.netty;

import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import net.potatocloud.api.property.PropertyData;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class PacketBuffer {

    private final ByteBuf buf;

    public void writeString(String string) {
        final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public String readString() {
        final byte[] bytes = new byte[buf.readInt()];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public void writeInt(int value) {
        buf.writeInt(value);
    }

    public int readInt() {
        return buf.readInt();
    }

    public void writeBoolean(boolean bool) {
        buf.writeBoolean(bool);
    }

    public boolean readBoolean() {
        return buf.readBoolean();
    }

    public void writeStringList(List<String> list) {
        writeInt(list.size());
        for (String item : list) {
            writeString(item);
        }
    }

    public List<String> readStringList() {
        final int size = readInt();
        final List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(readString());
        }
        return list;
    }

    public void writeObject(Object object) {
        if (object instanceof String string) {
            buf.writeByte(1);
            writeString(string);
        } else if (object instanceof Integer integer) {
            buf.writeByte(2);
            writeInt(integer);
        } else if (object instanceof Boolean bool) {
            buf.writeByte(3);
            writeBoolean(bool);
        } else if (object instanceof Long l) {
            buf.writeByte(4);
            writeLong(l);
        } else {
            throw new IllegalArgumentException("Unsupported object: " + object.getClass());
        }
    }


    public Object readObject() {
        final byte type = buf.readByte();
        return switch (type) {
            case 1 -> readString();
            case 2 -> readInt();
            case 3 -> readBoolean();
            case 4 -> readLong();
            default -> throw new IllegalArgumentException("Unknown object id: " + type);
        };
    }

    public void writePropertyData(PropertyData data) {
        writeString(data.getName());
        writeObject(data.getDefaultValue());
        writeObject(data.getValue());
    }

    public PropertyData readPropertyData() {
        final String name = readString();
        final Object defaultValue = readObject();
        final Object value = readObject();

        return new PropertyData(name, defaultValue, value);
    }

    public void writePropertyDataSet(Set<PropertyData> data) {
        buf.writeInt(data.size());
        for (PropertyData propertyData : data) {
            writePropertyData(propertyData);
        }
    }

    public Set<PropertyData> readPropertyDataSet() {
        final int size = readInt();
        final Set<PropertyData> dataSet = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            dataSet.add(readPropertyData());
        }
        return dataSet;
    }

    public void writeLong(long value) {
        buf.writeLong(value);
    }

    public long readLong() {
        return buf.readLong();
    }
}
