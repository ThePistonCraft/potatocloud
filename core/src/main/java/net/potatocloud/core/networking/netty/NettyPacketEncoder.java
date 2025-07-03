package net.potatocloud.core.networking.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketManager;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class NettyPacketEncoder extends MessageToByteEncoder<Packet> {

    private final PacketManager packetManager;

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        String json = packetManager.encode(packet);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
