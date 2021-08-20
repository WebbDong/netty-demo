package com.webbdong.rpc.server.boot.netty;

import com.webbdong.rpc.core.netty.codec.FrameDecoder;
import com.webbdong.rpc.core.netty.codec.FrameEncoder;
import com.webbdong.rpc.core.netty.codec.RpcRequestDecoder;
import com.webbdong.rpc.core.netty.codec.RpcResponseEncoder;
import com.webbdong.rpc.core.netty.handler.RpcRequestHandler;
import com.webbdong.rpc.core.spring.SpringBeanFactory;
import com.webbdong.rpc.server.boot.RpcServer;
import com.webbdong.rpc.server.prop.RpcServerProp;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于 Netty 实现的 RPC 服务端
 * @author Webb Dong
 * @date 2021-08-19 11:12 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
@Slf4j
public class NettyServer implements RpcServer {

    private final RpcServerProp prop;

    private final SpringBeanFactory springBeanFactory;

    @Override
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
        // 线程数传 0 表示使用默认线程数
        EventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        UnorderedThreadPoolEventExecutor business = new UnorderedThreadPoolEventExecutor(
                NettyRuntime.availableProcessors() * 2, new DefaultThreadFactory("business"));
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        RpcRequestHandler rpcRequestHandler = new RpcRequestHandler(springBeanFactory);
        try {
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("LoggingHandler", new LoggingHandler(LogLevel.INFO))
                                    .addLast("FrameEncoder", new FrameEncoder())
                                    .addLast("RpcResponseEncoder", new RpcResponseEncoder())
                                    .addLast("FrameDecoder", new FrameDecoder())
                                    .addLast("RpcRequestDecoder", new RpcRequestDecoder())
                                    .addLast("RpcRequestHandler", rpcRequestHandler);
                        }
                    });
            serverBootstrap.bind(prop.getRpcPort())
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (Exception e) {
            log.error("rpc server start error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            business.shutdownGracefully();
        }
    }

}
