package com.webbdong.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author: Webb Dong
 * @date: 2021-07-18 11:38 PM
 */
public class ServerInboundHandler1 extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler1 channelRegistered-----");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler1 channelUnregistered-----");
        super.channelUnregistered(ctx);
    }

    /**
     * 连接已建立好，通道初始化完成后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler1 channelActive-----");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler1 channelInactive-----");
        super.channelInactive(ctx);
    }

    /**
     * 通道有数据可读时调用
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ServerInboundHandler1 channelRead-----, remoteAddress = "
                + ctx.channel().remoteAddress());
        // 获取发送的客户端数据
        // ByteBuf 是 Netty 对 Java NIO 的 ByteBuffer 进行的封装
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("ServerInboundHandler1:received client data = "
                + buf.toString(StandardCharsets.UTF_8));
        super.channelRead(ctx, msg);
    }

    /**
     * 通道内数据读取完成后调用
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler1 channelReadComplete-----");
        // 向客户端发送数据
        String msg = "Hello Netty Client";
        ByteBuf buf = ctx.alloc().buffer();
        buf.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        // ctx.writeAndFlush 事件会从当前handler流向头部
        ctx.writeAndFlush(buf);
        // ctx.channel().writeAndFlush 事件会从pipeline尾部流向头部
//        ctx.channel().writeAndFlush(Unpooled.copiedBuffer(msg.getBytes(StandardCharsets.UTF_8)));
        super.channelReadComplete(ctx);
    }

    /**
     *
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("ServerInboundHandler1 userEventTriggered-----");
        super.userEventTriggered(ctx, evt);
    }

    /**
     *
     */
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ServerInboundHandler1 channelWritabilityChanged-----");
        super.channelWritabilityChanged(ctx);
    }

    /**
     * 异常回调
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("ServerInboundHandler1 exceptionCaught-----, cause = "
                + cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

}
