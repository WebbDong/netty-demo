package com.webbdong.netty.handler;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author Webb Dong
 * @date 2021-08-17 11:32 PM
 */
public class ClientWriteCheckIdleHandler extends IdleStateHandler {

    public ClientWriteCheckIdleHandler() {
        // 5秒没有向服务器发送数据，触发 idle
        super(0, 5, 0, TimeUnit.SECONDS);
    }

}
