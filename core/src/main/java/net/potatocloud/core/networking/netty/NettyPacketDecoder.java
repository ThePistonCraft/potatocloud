package net.potatocloud.core.networking.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketManager;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class NettyPacketDecoder extends ByteToMessageDecoder {

    private final PacketManager packetManager;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }

        in.markReaderIndex();
        final int length = in.readInt();

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }

        final byte[] bytes = new byte[length];
        in.readBytes(bytes);

        final String json = new String(bytes, StandardCharsets.UTF_8);
        final Packet packet = packetManager.decode(json);
        out.add(packet);
    }
}
