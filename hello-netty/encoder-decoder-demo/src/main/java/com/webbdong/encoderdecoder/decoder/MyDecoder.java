package com.webbdong.encoderdecoder.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;

import java.util.List;

/**
 * 自定义一次解码器，实现简单的以 $ 为分隔符的解码器
 * @author Webb Dong
 * @date 2021-08-16 12:46 AM
 */
public class MyDecoder extends ByteToMessageDecoder {

    private static final ByteProcessor PROCESSOR = new ByteProcessor.IndexOfProcessor((byte) '$');

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int eol = in.forEachByte(PROCESSOR);
        if (eol >= 0) {
            out.add(in.readRetainedSlice(eol - in.readerIndex()));
            // 将 $ 字符忽略掉
            in.skipBytes(1);
        }
    }

}
