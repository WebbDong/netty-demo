package com.webbdong.codec.protostuff;

import com.webbdong.codec.model.Message;
import com.webbdong.codec.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Protostuff 二次解码器
 * @author Webb Dong
 * @date 2021-08-16 5:21 PM
 */
public class ProtostuffDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        Message message = ProtostuffUtil.deserialize(bytes, Message.class);
        out.add(message);
    }

}
