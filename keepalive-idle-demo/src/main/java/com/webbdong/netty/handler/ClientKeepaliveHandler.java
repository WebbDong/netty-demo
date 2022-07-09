package com.webbdong.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author Webb Dong
 * @date 2021-08-18 1:57 AM
 */
public class ClientKeepaliveHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            System.out.println("client write idle, so send keepalive msg to server");
            ctx.writeAndFlush("this is keepalive msg");
        }
        super.userEventTriggered(ctx, evt);
    }

}
