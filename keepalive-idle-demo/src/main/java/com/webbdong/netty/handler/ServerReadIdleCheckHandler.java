package com.webbdong.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 服务端 read idle 空闲连接检测，超过 10 秒没有收到读事件，断开客户端连接
 * @author Webb Dong
 * @date 2021-08-17 9:55 PM
 */
public class ServerReadIdleCheckHandler extends IdleStateHandler {

    public ServerReadIdleCheckHandler() {
        // 10 秒没有收到读事件，触发 idel
        super(10, 0, 0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            ctx.close();
            System.out.println("server read idle , close channel.....");
            return;
        }
        super.channelIdle(ctx, evt);
    }

}
