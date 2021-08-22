package com.webbdong.rpc.core.netty.codec;

import com.webbdong.rpc.core.model.RpcRequest;
import com.webbdong.rpc.core.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-21 2:16 PM
 */
@Slf4j
public class RpcRequestEncoder extends MessageToMessageEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, List<Object> out) {
        try {
            byte[] bytes = ProtostuffUtil.serialize(msg);
            ByteBuf buf = ctx.alloc().buffer(bytes.length);
            buf.writeBytes(bytes);
            out.add(buf);
        } catch (Exception e) {
            log.error("RpcRequestEncoder error", e);
            throw new RuntimeException(e);
        }
    }

}
