package com.webbdong.rpc.core.netty.request;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;

/**
 * @author Webb Dong
 * @date 2021-08-19 1:00 PM
 */
public class RequestPromise extends DefaultPromise {

    public RequestPromise(EventExecutor executor) {
        super(executor);
    }

}
