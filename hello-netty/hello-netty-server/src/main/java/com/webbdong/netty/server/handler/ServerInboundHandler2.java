package com.webbdong.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author: Webb Dong
 * @date: 2021-07-18 11:38 PM
 */
public class ServerInboundHandler2 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler2 channelRegistered-----");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler2 channelUnregistered-----");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler2 channelActive-----");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler2 channelInactive-----");
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ServerInboundHandler2 channelRead-----");
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("ServerInboundHandler2:received client data = "
                + buf.toString(StandardCharsets.UTF_8));
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler2 channelReadComplete-----");
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("ServerInboundHandler2 userEventTriggered-----");
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler2 channelWritabilityChanged-----");
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("ServerInboundHandler2 exceptionCaught-----, cause = "
                + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

}
