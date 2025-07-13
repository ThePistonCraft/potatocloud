package net.potatocloud.core.networking.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.*;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@RequiredArgsConstructor
public class NettyNetworkServer implements NetworkServer {

    private final PacketManager packetManager;
    private final List<NetworkConnection> connectedSessions = new CopyOnWriteArrayList<>();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int port;

    @Override
    public void start(String hostname, int port) {
        PacketRegistry.registerPackets(packetManager);

        this.port = port;
        bossGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

        final ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new NettyPacketDecoder(packetManager));
                        pipeline.addLast(new NettyPacketEncoder(packetManager));
                        pipeline.addLast(new NettyServerHandler(NettyNetworkServer.this, packetManager));
                    }
                });

        final ChannelFuture future = server.bind(new InetSocketAddress(hostname, port)).syncUninterruptibly();
        serverChannel = future.channel();
    }

    @Override
    public void shutdown() {
        for (NetworkConnection session : connectedSessions) {
            session.close();
        }
        serverChannel.close();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public boolean isRunning() {
        return serverChannel != null && serverChannel.isActive();
    }

    @Override
    public <T extends Packet> void registerPacketListener(String packetType, PacketListener<T> listener) {
        packetManager.registerListener(packetType, listener);
    }

    @Override
    public void sendToClient(NetworkConnection client, Packet packet) {
        client.send(packet);
    }
}
