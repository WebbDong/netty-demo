package com.webbdong.codec.protostuff;

import com.webbdong.codec.model.Message;
import com.webbdong.codec.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Protostuff 二次编码器
 * @author Webb Dong
 * @date 2021-08-16 5:21 PM
 */
public class ProtostuffEncoder extends MessageToMessageEncoder<Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        byte[] bytes = ProtostuffUtil.serialize(msg);
        ByteBuf byteBuf = ctx.alloc().buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        out.add(byteBuf);
    }

}
