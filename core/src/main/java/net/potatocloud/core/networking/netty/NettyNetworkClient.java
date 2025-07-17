package net.potatocloud.core.networking.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.*;

@RequiredArgsConstructor
public class NettyNetworkClient implements NetworkClient {

    private final PacketManager packetManager;
    private EventLoopGroup group;
    private Channel channel;

    @Override
    public void connect(String host, int port) {
        PacketRegistry.registerPackets(packetManager);

        group = new NioEventLoopGroup();

        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new NettyPacketDecoder(packetManager));
                        pipeline.addLast(new NettyPacketEncoder(packetManager));
                        pipeline.addLast(new NettyClientHandler(packetManager));
                    }
                });

        final ChannelFuture future = bootstrap.connect(host, port).syncUninterruptibly();
        channel = future.channel();
    }

    @Override
    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public void disconnect() {
        channel.close();
        group.shutdownGracefully();
    }

    @Override
    public <T extends Packet> void registerPacketListener(String packetType, PacketListener<T> listener) {
        packetManager.registerListener(packetType, listener);
    }

    @Override
    public boolean isConnected() {
        return channel != null && channel.isActive();
    }
}
