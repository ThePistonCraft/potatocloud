package net.potatocloud.core.networking;

import com.google.gson.*;
import lombok.Getter;
import java.util.*;

@Getter
public class PacketManager {

    private final Map<String, Class<? extends Packet>> packets = new HashMap<>();
    private final Map<Class<? extends Packet>, PacketListener<?>> listeners = new HashMap<>();
    private final Gson gson = new Gson();

    public void register(String type, Class<? extends Packet> clazz) {
        packets.put(type, clazz);
    }

    public <T extends Packet> void registerListener(Class<T> clazz, PacketListener<T> listener) {
        listeners.put(clazz, listener);
    }

    public Packet decode(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        String type = obj.get("type").getAsString();
        Class<? extends Packet> clazz = packets.get(type);
        return gson.fromJson(json, clazz);
    }

    public String encode(Packet packet) {
        JsonObject obj = gson.toJsonTree(packet).getAsJsonObject();
        obj.addProperty("type", packet.getType());
        return gson.toJson(obj);
    }

    public void onPacket(NetworkConnection connection, Packet packet) {
        PacketListener listener = listeners.get(packet.getClass());
        if (listener != null) {
            listener.onPacket(connection, packet);
        }
    }
}
