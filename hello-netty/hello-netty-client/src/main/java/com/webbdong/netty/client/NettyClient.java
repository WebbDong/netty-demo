package com.webbdong.netty.client;

import com.webbdong.netty.client.handler.ClientInboundHandler1;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

/**
 * Netty 客户端
 * @author: Webb Dong
 * @date: 2021-07-22 4:06 PM
 */
public class NettyClient {

    public static void main(String[] args) {
        final NettyClient client = new NettyClient();
        client.start("127.0.0.1", 30000);
    }

    @SneakyThrows
    public void start(final String host, final int port) {
        final EventLoopGroup clientGroup = new NioEventLoopGroup();
        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ClientInboundHandler1());
                        }
                    });
            final ChannelFuture future = bootstrap.connect(host, port).sync();
            // 获取到 Channel 之后可以调用 writeAndFlush 发送数据
            future.channel().closeFuture().sync();
        } finally {
            clientGroup.shutdownGracefully();
        }
    }

}
