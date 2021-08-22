package com.webbdong.rpc.core.netty.codec;

import com.webbdong.rpc.core.model.RpcResponse;
import com.webbdong.rpc.core.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-21 4:05 PM
 */
@Slf4j
public class RpcResponseDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            byte[] bytes = new byte[msg.readableBytes()];
            msg.readBytes(bytes);
            RpcResponse rpcResponse = ProtostuffUtil.deserialize(bytes, RpcResponse.class);
            out.add(rpcResponse);
        } catch (Exception e) {
            log.error("RpcResponseDecoder decode error", e);
            throw new RuntimeException(e);
        }
    }

}
