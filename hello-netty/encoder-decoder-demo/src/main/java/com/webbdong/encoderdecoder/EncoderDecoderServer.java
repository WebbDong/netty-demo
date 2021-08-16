package com.webbdong.encoderdecoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * TCP 粘包、拆包(半包)
 *      粘包
 *          发送方发送的若干包数据到达接收方时粘成了一包，从接收缓冲区来看，后一包数据的头紧接着前一包数据的尾
 *
 *      拆包(半包)
 *          发送方发送数据大于套接字缓冲区大小，就会将一个数据包拆成多个最大 TCP 长度的 TCP 报文分开传输
 *
 *      从两个角度看粘包和拆包
 *          1. 收发角度：一个发送可能被多次接收(拆包)，多个发送可能被一次接收(粘包)
 *          2. 传输角度：一个发送可能占用多个传输包(拆包)，多个发送可能公用一个传输包(粘包)
 *
 *      根本原因
 *          TCP 协议是面向连接的、可靠的、基于字节流的传输层 通信协议，是一种流式协议，消息无边界。
 *
 * TCP 粘包，拆包解决方案
 *      解决TCP粘包，半包问题的根本：找出消息的边界
 *
 *      1. TCP 连接变成短连接，一个请求一个短连接，效率和性能低下，不推荐
 *      2. 封装成帧
 *          方式一：固定长度，长度不好定义，会造成空间浪费，不推荐
 *          方式二：分隔符
 *          方式三：固定长度，字段存消息长度，将消息分为消息头和消息体，在消息头中存储着消息体的长度，消息头固定长度，消息体不固定长度
 *
 * Netty 如何解决粘包、拆包问题
 *      Netty 提供了针对封装成帧这种形式下不同方式的拆包器，所谓的拆包其实就是数据的解码,所谓解码就是将网络中的一些原始数据解码成上层应用的数据，
 *      那对应在发送数据的时候要按照同样的方式进行数据的编码操作然后发送到网络中
 *
 *      固定长度：FixedLengthFrameDecoder 解码器
 *      分隔符：DelimiterBasedFrameDecoder 解码器
 *      固定长度，字段存消息长度：LengthFieldPrepender 编码器、LengthFieldBasedFrameDecoder 解码器
 *
 * @author Webb Dong
 * @date 2021-08-13 1:23 PM
 */
public class EncoderDecoderServer {

    public static void main(String[] args) {
        final EncoderDecoderServer server = new EncoderDecoderServer();
        server.start(20000);
    }

    @SneakyThrows
    public void start(int port) {
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
                    .childHandler(new ChannelInitializer() {
                        @Override
                        public void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
//                                    .addLast(new LengthFieldPrepender(2))
//                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    .addLast(new ServerHandler());
                        }
                    });
            final ChannelFuture future = bootstrap.bind(port).sync();
            // 阻塞，监听服务端端口的关闭
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class ServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
//            fixedLengthFrameWrite(ctx);
//            lineBasedFrameWrite(ctx);
            delimiterBasedFrameDecoderWrite(ctx);
//            jsonWrite(ctx);
            super.channelActive(ctx);
        }

        /**
         * 固定长度解码器 FixedLengthFrameDecoder 对应的数据发送
         * @param ctx
         */
        private void fixedLengthFrameWrite(final ChannelHandlerContext ctx) {
            for (int i = 0; i < 100; i++) {
                ctx.writeAndFlush(Unpooled.wrappedBuffer("Hello".getBytes(StandardCharsets.UTF_8)));
            }
        }

        /**
         * 换行分隔符解码器 LineBasedFrameDecoder 对应的数据发送
         * @param ctx
         */
        private void lineBasedFrameWrite(final ChannelHandlerContext ctx) {
            for (int i = 0; i < 100; i++) {
                ctx.writeAndFlush(Unpooled.wrappedBuffer("Hello\n".getBytes(StandardCharsets.UTF_8)));
            }
        }

        /**
         * 自定义分隔符解码器 DelimiterBasedFrameDecoder 对应的数据发送
         * @param ctx
         */
        private void delimiterBasedFrameDecoderWrite(final ChannelHandlerContext ctx) {
            for (int i = 0; i < 100; i++) {
                ctx.writeAndFlush(Unpooled.wrappedBuffer("Hello$".getBytes(StandardCharsets.UTF_8)));
            }
        }

        /**
         * JsonObjectDecoder 对应的数据发送
         * @param ctx
         */
        @SneakyThrows
        private void jsonWrite(ChannelHandlerContext ctx) {
            Map<String, Object> map = new HashMap<>();
            ObjectMapper objectMapper = new ObjectMapper();
            for (int i = 0; i < 100; i++) {
                map.put("id", i);
                map.put("name", i);
                ctx.writeAndFlush(Unpooled.wrappedBuffer(objectMapper.writeValueAsBytes(map)));
            }
        }

    }

}
