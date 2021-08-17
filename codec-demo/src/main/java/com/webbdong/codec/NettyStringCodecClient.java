package com.webbdong.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

/**
 * @author Webb Dong
 * @date 2021-08-16 11:43 AM
 */
public class NettyStringCodecClient {

    public static void main(String[] args) {
        final NettyStringCodecClient client = new NettyStringCodecClient();
        client.start("127.0.0.1", 20000);
    }

    @SneakyThrows
    public void start(final String host, final int port) {
        final EventLoopGroup clientGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(clientGroup)
                    .channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldPrepender(2))
                                    // String 二次编码器
                                    .addLast(new StringEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    // String 二次解码器
                                    .addLast(new StringDecoder())
                                    .addLast(new ClientHandler());
                        }
                    });
            final ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            clientGroup.shutdownGracefully();
        }
    }

    private static class ClientHandler extends SimpleChannelInboundHandler<String> {

        private int count = 1;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("第" + count++ + "次获取到服务端数据: " + msg);
        }

    }

}
