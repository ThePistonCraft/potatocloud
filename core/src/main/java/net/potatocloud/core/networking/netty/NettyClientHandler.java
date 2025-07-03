package net.potatocloud.core.networking.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.Packet;
import net.potatocloud.core.networking.PacketManager;

@RequiredArgsConstructor
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final PacketManager packetManager;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Packet packet) {
            NetworkConnection connection = new NettyNetworkConnection(ctx.channel());
            packetManager.onPacket(connection, packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
