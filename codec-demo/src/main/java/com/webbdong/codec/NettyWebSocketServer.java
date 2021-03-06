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
                                    // ????????????????????????????????????????????????
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
            // ????????? http ?????????????????? websocket ??????
            if (msg instanceof FullHttpRequest) {
                if (handleHttpRequest(ctx, (FullHttpRequest) msg)) {
                    // ??????????????????????????????????????????????????????5s????????????
                    new Thread(() -> {
                        while (true) {
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(
                                    "??????, ??????????????????????????????????????????, ???????????????:" + new Date().toString()));
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
            // ????????????????????????
            if (frame instanceof CloseWebSocketFrame) {
                // ??????????????????
                serverHandshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
                return;
            }
            if (frame instanceof PingWebSocketFrame) {
                // ??????????????? ping ??????
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            if (frame instanceof TextWebSocketFrame) {
                // ??????????????????
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                String text = textFrame.text();
                System.out.println("receive text msg:" + text);
                // ????????????
                ctx.channel().writeAndFlush(new TextWebSocketFrame(
                        "??????,????????????netty websocket ??????,????????? ??????:" + new Date().toString()));
                return;
            }
            if (frame instanceof BinaryWebSocketFrame) {
                // ???????????????
                System.out.println("BinaryWebSocketFrame");
            }
        }

        private boolean handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
            // ???????????????????????????
            if (!request.decoderResult().isSuccess()) {
                sendHttpResponse(ctx, request,
                        new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return false;
            }
            // ??????????????????????????? websocket ???????????????????????????????????????????????????????????????????????????????????????
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
