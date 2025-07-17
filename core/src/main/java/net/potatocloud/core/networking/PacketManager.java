package net.potatocloud.core.networking;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketManager {

    private final Map<String, Class<? extends Packet>> packets = new HashMap<>();
    private final Map<String, List<PacketListener<? extends Packet>>> listeners = new HashMap<>();
    private final Gson gson = new Gson();

    public void register(String type, Class<? extends Packet> clazz) {
        packets.put(type, clazz);
    }

    public <T extends Packet> void registerListener(String packetType, PacketListener<T> listener) {
        listeners.computeIfAbsent(packetType, key -> new CopyOnWriteArrayList<>()).add(listener);
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
        List<PacketListener<? extends Packet>> listenerList = listeners.get(packet.getType());
        if (listenerList != null) {
            for (PacketListener<? extends Packet> listener : listenerList) {
                ((PacketListener<T>) listener).onPacket(connection, (T) packet);
            }
        }
    }
}