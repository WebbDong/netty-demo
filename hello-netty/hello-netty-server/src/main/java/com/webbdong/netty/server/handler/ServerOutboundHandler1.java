package com.webbdong.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author: Webb Dong
 * @date: 2021-07-18 11:38 PM
 */
public class ServerOutboundHandler1 extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("ServerOutboundHandler1.write()");
        super.write(ctx, msg, promise);
    }

}
