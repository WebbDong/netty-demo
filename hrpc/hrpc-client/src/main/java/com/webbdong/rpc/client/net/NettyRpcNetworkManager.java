package com.webbdong.rpc.client.net;

import com.webbdong.rpc.client.prop.RpcClientProp;
import com.webbdong.rpc.core.model.RpcRequest;
import com.webbdong.rpc.core.model.RpcResponse;
import com.webbdong.rpc.core.model.ServiceProvider;
import com.webbdong.rpc.core.netty.codec.FrameDecoder;
import com.webbdong.rpc.core.netty.codec.FrameEncoder;
import com.webbdong.rpc.core.netty.codec.RpcRequestEncoder;
import com.webbdong.rpc.core.netty.codec.RpcResponseDecoder;
import com.webbdong.rpc.core.netty.handler.RpcResponseHandler;
import com.webbdong.rpc.core.netty.request.RequestPromise;
import com.webbdong.rpc.core.netty.request.RpcRequestHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.FutureListener;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @date 2021-08-21 2:12 AM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
@Slf4j
public class NettyRpcNetworkManager extends BaseRpcNetworkManager {

    private final RpcClientProp rpcClientProp;

    @SneakyThrows
    @Override
    public RpcResponse networkRequest(RpcRequest request, ServiceProvider serviceProvider) {
        EventLoopGroup worker = new NioEventLoopGroup(0, new DefaultThreadFactory("worker"));
        Bootstrap bootstrap = new Bootstrap();
        RpcResponseHandler rpcResponseHandler = new RpcResponseHandler();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("LoggingHandler", new LoggingHandler(LogLevel.INFO))
                                .addLast("FrameEncoder", new FrameEncoder())
                                .addLast("RpcRequestEncoder", new RpcRequestEncoder())
                                .addLast("FrameDecoder", new FrameDecoder())
                                .addLast("RpcResponseDecoder", new RpcResponseDecoder())
                                .addLast("RpcResponseHandler", rpcResponseHandler);
                    }
                });
        ChannelFuture future = bootstrap.connect(serviceProvider.getServerIp(), serviceProvider.getRpcPort()).sync();
        // 判断连接成功
        if (future.isSuccess()) {
            RequestPromise requestPromise = new RequestPromise(future.channel().eventLoop());
            RpcRequestHolder.addRequestPromise(request.getRequestId(), requestPromise);
            // 向服务端发送数据
            ChannelFuture channelFuture = future.channel().writeAndFlush(request);
            // 添加发送数据结果回调监听
            channelFuture.addListener((ChannelFutureListener) f -> {
                // 如果没有发送成功，移除 RequestPromise
                if (!f.isSuccess()) {
                    RpcRequestHolder.removeRequestPromise(request.getRequestId());
                }
            });
            // 添加响应结果回调监听
            requestPromise.addListener((FutureListener<RpcResponse>) f -> {
                if (!f.isSuccess()) {
                    RpcRequestHolder.removeRequestPromise(request.getRequestId());
                }
            });
            // 阻塞获取 RpcResponse
            RpcResponse rpcResponse = (RpcResponse) requestPromise.get(
                    rpcClientProp.getConnectTimeout(), TimeUnit.MILLISECONDS);
            return rpcResponse;
        }
        return null;
    }

}
