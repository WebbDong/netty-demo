package com.webbdong.rpc.core.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author Webb Dong
 * @date 2021-08-20 12:03 AM
 */
public class FrameDecoder extends LengthFieldBasedFrameDecoder {

    public FrameDecoder() {
        super(Integer.MAX_VALUE, 0, 4, 0, 4);
    }

}
