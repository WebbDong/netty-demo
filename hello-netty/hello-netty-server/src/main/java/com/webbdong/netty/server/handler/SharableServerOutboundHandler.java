package com.webbdong.netty.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author: Webb Dong
 * @date: 2021-07-23 5:46 PM
 */
@ChannelHandler.Sharable
public class SharableServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("SharableServerOutboundHandler.write()");
        super.write(ctx, msg, promise);
    }

}
