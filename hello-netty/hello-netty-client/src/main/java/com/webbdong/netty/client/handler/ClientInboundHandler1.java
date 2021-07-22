package com.webbdong.netty.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author: Webb Dong
 * @date: 2021-07-22 4:55 PM
 */
public class ClientInboundHandler1 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 向服务端发送数据
        String msg = "Hello Netty Server";
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("ClientInboundHandler1:received server data = "
                + buf.toString(StandardCharsets.UTF_8));
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("ClientInboundHandler1 exceptionCaught-----, cause = "
                + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

}
