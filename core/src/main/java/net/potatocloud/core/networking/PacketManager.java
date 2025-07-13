package net.potatocloud.core.networking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.Map;

public class PacketManager {

    private final Map<String, Class<? extends Packet>> packets = new HashMap<>();
    private final Map<String, PacketListener<? extends Packet>> listeners = new HashMap<>();
    private final Gson gson = new Gson();

    public void register(String type, Class<? extends Packet> clazz) {
        packets.put(type, clazz);
    }

    public <T extends Packet> void registerListener(String packetType, PacketListener<T> listener) {
        listeners.put(packetType, listener);
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

    @SuppressWarnings("unchecked")
    public <T extends Packet> void onPacket(NetworkConnection connection, Packet packet) {
        PacketListener<T> listener = (PacketListener<T>) listeners.get(packet.getType());
        if (listener != null) {
            listener.onPacket(connection, (T) packet);
        }
    }
}
