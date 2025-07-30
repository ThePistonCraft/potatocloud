package net.potatocloud.core.networking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class PacketManager {

    private final Map<Integer, Supplier<? extends Packet>> packets = new HashMap<>();
    private final Map<Integer, List<PacketListener<? extends Packet>>> listeners = new HashMap<>();

    public void register(int id, Supplier<? extends Packet> packet) {
        packets.put(id, packet);
    }

    public <T extends Packet> void registerListener(int id, PacketListener<T> listener) {
        listeners.computeIfAbsent(id, key -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public Packet createPacket(int id) {
        return packets.get(id).get();
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void onPacket(NetworkConnection connection, Packet packet) {
        final List<PacketListener<? extends Packet>> packetListeners = listeners.get(packet.getId());
        if (packetListeners == null) {
            return;
        }
        for (PacketListener<? extends Packet> listener : packetListeners) {
            ((PacketListener<T>) listener).onPacket(connection, (T) packet);
        }
    }
}
