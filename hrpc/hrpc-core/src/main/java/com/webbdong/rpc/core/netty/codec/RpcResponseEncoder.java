package com.webbdong.rpc.core.netty.codec;

import com.webbdong.rpc.core.model.RpcResponse;
import com.webbdong.rpc.core.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-20 12:32 AM
 */
@Slf4j
public class RpcResponseEncoder extends MessageToMessageEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, List<Object> out) throws Exception {
        try {
            byte[] bytes = ProtostuffUtil.serialize(msg);
            ByteBuf byteBuf = ctx.alloc().buffer(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(byteBuf);
        } catch (Exception e) {
            log.error("RpcResponseEncoder encode error", e);
            throw new RuntimeException(e);
        }
    }

}
