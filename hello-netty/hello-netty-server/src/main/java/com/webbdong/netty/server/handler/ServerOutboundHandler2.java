package com.webbdong.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author: Webb Dong
 * @date: 2021-07-22 5:14 PM
 */
public class ServerOutboundHandler2 extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("ServerOutboundHandler2.write()");
        super.write(ctx, msg, promise);
    }

}
