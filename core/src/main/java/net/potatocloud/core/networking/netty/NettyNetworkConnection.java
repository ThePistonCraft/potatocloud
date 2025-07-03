package net.potatocloud.core.networking.netty;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.potatocloud.core.networking.NetworkConnection;
import net.potatocloud.core.networking.Packet;

@Getter
@RequiredArgsConstructor
public class NettyNetworkConnection implements NetworkConnection {

    private final Channel channel;

    @Override
    public void send(Packet packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public void close() {
        channel.close();
    }
}
