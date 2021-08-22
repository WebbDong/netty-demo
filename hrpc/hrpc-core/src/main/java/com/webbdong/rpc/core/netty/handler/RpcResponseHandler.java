package com.webbdong.rpc.core.netty.handler;

import com.webbdong.rpc.core.model.RpcResponse;
import com.webbdong.rpc.core.netty.request.RequestPromise;
import com.webbdong.rpc.core.netty.request.RpcRequestHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 响应处理器
 * @author Webb Dong
 * @date 2021-08-21 4:55 PM
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        log.info("---rpc调用成功，返回结果为:{}", msg);
        RequestPromise requestPromise = RpcRequestHolder.getRequestPromise(msg.getRequestId());
        if (requestPromise != null) {
            // 通过 Promise 通知
            requestPromise.setSuccess(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
