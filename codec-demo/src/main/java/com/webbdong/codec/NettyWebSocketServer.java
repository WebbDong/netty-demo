package com.webbdong.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @date 2021-08-17 1:38 PM
 */
public class NettyWebSocketServer {

    public static void main(String[] args) {
        final NettyWebSocketServer server = new NettyWebSocketServer();
        server.start(20005);
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
                                    .addLast(new HttpServerCodec())
                                    // 文件上传需要设置大点，单位是字节
                                    .addLast(new HttpObjectAggregator(1024 * 1024 * 8))
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

        private WebSocketServerHandshaker serverHandshaker;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 判断是 http 握手请求还是 websocket 请求
            if (msg instanceof FullHttpRequest) {
                if (handleHttpRequest(ctx, (FullHttpRequest) msg)) {
                    // 握手成功后，服务端主动推送消息，每隔5s推送一次
                    new Thread(() -> {
                        while (true) {
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(
                                    "你好, 这是服务器主动推送回来的数据, 当前时间为:" + new Date().toString()));
                            try {
                                TimeUnit.SECONDS.sleep(5);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                }
            } else if (msg instanceof WebSocketFrame) {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
            super.channelRead(ctx, msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }

        private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
            // 判断链路消息类型
            if (frame instanceof CloseWebSocketFrame) {
                // 关闭链路指令
                serverHandshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
                return;
            }
            if (frame instanceof PingWebSocketFrame) {
                // 维持链路的 ping 指令
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            if (frame instanceof TextWebSocketFrame) {
                // 普通文本消息
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                String text = textFrame.text();
                System.out.println("receive text msg:" + text);
                // 构造响应
                ctx.channel().writeAndFlush(new TextWebSocketFrame(
                        "你好,欢迎使用netty websocket 服务,当前时 间为:" + new Date().toString()));
                return;
            }
            if (frame instanceof BinaryWebSocketFrame) {
                // 二进制消息
                System.out.println("BinaryWebSocketFrame");
            }
        }

        private boolean handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
            // 先判断是否解码成功
            if (!request.decoderResult().isSuccess()) {
                sendHttpResponse(ctx, request,
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return false;
            }
            // 然后判断是否要建立 websocket 连接，构造握手工厂创建握手处理类，并且构造握手响应给客户端
            if (!request.headers().contains(HttpHeaderNames.UPGRADE, "websocket", true)) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                return false;
            }
            WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory(
                    "ws://localhost:20005/websocket", null, false);
            serverHandshaker = handshakerFactory.newHandshaker(request);
            serverHandshaker.handshake(ctx.channel(), request);
            return true;
        }

        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
            if (!response.status().equals(HttpResponseStatus.OK)) {
                ByteBuf byteBuf = Unpooled.wrappedBuffer("error".getBytes(StandardCharsets.UTF_8));
                response.content().writeBytes(byteBuf);
                byteBuf.release();
                HttpUtil.setContentLength(response, response.content().readableBytes());
            }
            ChannelFuture future = ctx.channel().writeAndFlush(response);
            if (!HttpUtil.isKeepAlive(request) || !response.status().equals(HttpResponseStatus.OK)) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }

    }

}
