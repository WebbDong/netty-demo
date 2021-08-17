package com.webbdong.encoderdecoder;

import com.webbdong.encoderdecoder.decoder.MyDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ByteProcessor;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-13 1:23 PM
 */
public class EncoderDecoderClient {

    public static void main(String[] args) {
        final EncoderDecoderClient client = new EncoderDecoderClient();
        client.start("127.0.0.1", 20000);
    }

    @SneakyThrows
    public void start(final String host, final int port) {
        final EventLoopGroup clientGroup = new NioEventLoopGroup();
        try {
            final Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 固定长度，字段存消息长度编码器，与 LengthFieldBasedFrameDecoder 配套使用
//                                    .addLast(new LengthFieldPrepender(2))

                                    // 固定长度解码器，按照每个数据包 5 个字节读取
//                                    .addLast(new FixedLengthFrameDecoder(5))
                                    // 换行分隔符解码器，maxLength 构造参数指定消息的最大长度，超过该长度还未找到对应的分隔符就会抛出 TooLongFrameException
//                                    .addLast(new LineBasedFrameDecoder(10))
                                    // 自定义分隔符解码器，maxFrameLength 构造参数指定消息的最大长度，超过该长度还未找到对应的分隔符就会抛出 TooLongFrameException
//                                    .addLast(new DelimiterBasedFrameDecoder(10, Unpooled.wrappedBuffer("$".getBytes(StandardCharsets.UTF_8))))
                                    // 固定长度，字段存消息长度解码器
//                                    .addLast(new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2))
                                    // JSON 解码器
//                                    .addLast(new JsonObjectDecoder())
                                    .addLast(new MyDecoder())
                                    .addLast(new ClientHandler());
                        }
                    });
            final ChannelFuture future = bootstrap.connect(host, port).sync();
            // 获取到 Channel 之后可以调用 writeAndFlush 发送数据
            future.channel().closeFuture().sync();
        } finally {
            clientGroup.shutdownGracefully();
        }
    }

    private static class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private int count = 1;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println("第" + count++ + "次获取到服务端数据: " + msg.toString(StandardCharsets.UTF_8));
        }

    }

}
