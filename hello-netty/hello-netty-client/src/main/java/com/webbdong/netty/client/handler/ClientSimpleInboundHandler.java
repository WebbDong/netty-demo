package com.webbdong.netty.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;

/**
 * @author Webb Dong
 * @date 2021-08-09 4:23 PM
 */
public class ClientSimpleInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = "Hello Netty Server";
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("ClientSimpleInboundHandler:received server data = "
                + msg.toString(StandardCharsets.UTF_8));
    }

}
