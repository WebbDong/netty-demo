package com.webbdong.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * 共享复用 ChannelInboundHandler
 * @author: Webb Dong
 * @date: 2021-07-23 4:10 PM
 */
@ChannelHandler.Sharable
public class SharableServerInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SharableServerInboundHandler channelRead-----");
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("SharableServerInboundHandler:received client data = "
                + buf.toString(StandardCharsets.UTF_8));
        super.channelRead(ctx, msg);
    }

}
