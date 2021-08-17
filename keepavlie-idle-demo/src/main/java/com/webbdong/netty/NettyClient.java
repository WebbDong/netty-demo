package com.webbdong.netty;

import com.webbdong.netty.handler.ClientKeepaliveHandler;
import com.webbdong.netty.handler.ClientWriteCheckIdleHandler;
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
 * 客户端添加 write idle check + keepalive，5 秒不发送数据给服务端就发送一个 keepalive，避免连接被断，也避免频繁发送 keepalive
 * @author Webb Dong
 * @date 2021-08-17 8:36 PM
 */
public class NettyClient {

    public static void main(String[] args) {
        final NettyClient client = new NettyClient();
        client.start("127.0.0.1", 23000);
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
                                    // write idle 检测
                                    .addLast(new ClientWriteCheckIdleHandler())
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast(new StringEncoder())
                                    // 当触发 write idle 时发送 keepalvie 消息给服务端
                                    .addLast(new ClientKeepaliveHandler())
                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
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
