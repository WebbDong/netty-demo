package com.webbdong.codec;

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
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

/**
 * 二次编解码
 *      我们把解决半包粘包问题的常用三种解码器叫一次解码器，其作用是将原始数据流(可能会出现粘包和半包的数据流)
 *      转换为用户数据(ByteBuf中存储)，但仍然是字节数据，所以我们需要二次解码器将字节数组转换为 java 对象，
 *      或者将一种格式转化为另一种格式，方便上层应用程序使用。
 *
 *      一次解码器继承自：ByteToMessageDecoder；二次解码器继承自：MessageToMessageDecoder；
 *      但他们的本质都是继承 ChannelInboundHandlerAdapter
 *
 * 是不是也可以合并1 次解码和2 次解码？
 *      可以，但不推荐。没有分层，不够清晰; 耦合性高，不容易置换方案
 * @author Webb Dong
 * @date 2021-08-16 11:43 AM
 */
public class NettyStringCodecServer {

    public static void main(String[] args) {
        final NettyStringCodecServer server = new NettyStringCodecServer();
        server.start(20000);
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
                                    // String 二次编码器
                                    .addLast(new StringEncoder())
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    // String 二次解码器
                                    .addLast(new StringDecoder())
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
                ctx.writeAndFlush("Hello" + i);
            }
            super.channelActive(ctx);
        }

    }

}
