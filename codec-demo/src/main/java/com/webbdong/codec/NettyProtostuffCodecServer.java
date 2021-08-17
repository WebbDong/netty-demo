package com.webbdong.codec;

import com.webbdong.codec.model.Message;
import com.webbdong.codec.model.User;
import com.webbdong.codec.protostuff.ProtostuffDecoder;
import com.webbdong.codec.protostuff.ProtostuffEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

/**
 * @author Webb Dong
 * @date 2021-08-16 5:11 PM
 */
public class NettyProtostuffCodecServer {

    public static void main(String[] args) {
        final NettyProtostuffCodecServer server = new NettyProtostuffCodecServer();
        server.start(20002);
    }

    @SneakyThrows
    public void start(final int port) {
        final EventLoopGroup boss = new NioEventLoopGroup(1);
        final EventLoopGroup worker = new NioEventLoopGroup();
        final ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldPrepender(2))
                                    // Protostuff 二次编码器
                                    .addLast(new ProtostuffEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    // Protostuff 二次解码器
                                    .addLast(new ProtostuffDecoder())
                                    .addLast(new ServerHandler());
                        }
                    });
            final ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class ServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 0; i < 100; i++) {
                User user = User.builder()
                        .id((long) i)
                        .name("name" + i)
                        .build();
                Message<User> msg = Message.<User>builder()
                        .code(200)
                        .msg("SUCCESS")
                        .data(user)
                        .build();
                ctx.writeAndFlush(msg);
            }
            super.channelActive(ctx);
        }

    }

}
