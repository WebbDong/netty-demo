package com.webbdong.codec;

import com.webbdong.codec.protobuf.MessageProto;
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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

/**
 * @author Webb Dong
 * @date 2021-08-16 2:47 PM
 */
public class NettyProtobufCodecServer {

    public static void main(String[] args) {
        final NettyProtobufCodecServer server = new NettyProtobufCodecServer();
        server.start(20001);
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
                                    // Protobuf 二次编码器
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    // Protobuf 二次解码器
                                    .addLast(new ProtobufDecoder(MessageProto.Message.getDefaultInstance()))
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
            MessageProto.Message msg;
            for (int i = 0; i < 100; i++) {
                msg = MessageProto.Message.newBuilder()
                        .setId("msg" + i)
                        .setContent("Hello Protobuf" + i)
                        .build();
                ctx.writeAndFlush(msg);
            }
            super.channelActive(ctx);
        }

    }

}
