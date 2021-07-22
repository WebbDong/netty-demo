package com.webbdong.netty.server;

import com.webbdong.netty.server.handler.ServerInboundHandler1;
import com.webbdong.netty.server.handler.ServerInboundHandler2;
import com.webbdong.netty.server.handler.ServerOutboundHandler1;
import com.webbdong.netty.server.handler.ServerOutboundHandler2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

/**
 * Netty 服务端
 * @author: Webb Dong
 * @date: 2021-07-18 12:54 AM
 */
public class NettyServer {

    public static void main(String[] args) {
        final NettyServer server = new NettyServer();
        server.start(30000);
    }

    @SneakyThrows
    public void start(final int port) {
        final EventLoopGroup boss = new NioEventLoopGroup(1);
        final EventLoopGroup worker = new NioEventLoopGroup();
        try {
            // 创建服务端引导类，引导整个服务端程序的启动
            final ServerBootstrap bootstrap = new ServerBootstrap();
            // 进行各种配置
            bootstrap.group(boss, worker)
                    // 设置服务端 channel，用于接收连接
                    .channel(NioServerSocketChannel.class)
                    // 给服务端添加 LoggingHandler
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ServerChannelInitializer());
            final ChannelFuture future = bootstrap.bind(port).sync();
            // 阻塞，监听服务端端口的关闭
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class ServerChannelInitializer extends ChannelInitializer {

        /**
         * initChannel 方法在每次客户端连接创建完成后为其初始化 handler 时执行
         */
        @Override
        protected void initChannel(Channel ch) throws Exception {
            // 向 pipeline 中添加自定义 handler
            // InboundHandler 执行顺序是顺序执行，OutboundHandler 执行顺序是逆序执行
            ch.pipeline()
                    .addLast(new ServerOutboundHandler1())
                    .addLast(new ServerOutboundHandler2())
                    .addLast(new ServerInboundHandler1())
                    .addLast(new ServerInboundHandler2());
        }
        
    }

}
