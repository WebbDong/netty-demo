package com.webbdong.rpc.core.netty.codec;

import com.webbdong.rpc.core.model.RpcRequest;
import com.webbdong.rpc.core.util.ProtostuffUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-20 12:29 AM
 */
@Slf4j
public class RpcRequestDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            byte[] bytes = new byte[msg.readableBytes()];
            msg.writeBytes(bytes);
            RpcRequest rpcRequest = ProtostuffUtil.deserialize(bytes, RpcRequest.class);
            out.add(rpcRequest);
        } catch (Exception e) {
            log.error("RpcRequestDecoder decode error", e);
            throw new RuntimeException(e);
        }
    }

}
