package com.webbdong.rpc.core.netty.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author Webb Dong
 * @date 2021-08-20 12:27 AM
 */
public class FrameEncoder extends LengthFieldPrepender {

    public FrameEncoder() {
        super(4, 0);
    }

}
