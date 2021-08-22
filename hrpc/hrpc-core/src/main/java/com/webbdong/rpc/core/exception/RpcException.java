package com.webbdong.rpc.core.exception;

import com.webbdong.rpc.core.enums.StatusEnum;

/**
 * @author Webb Dong
 * @date 2021-08-19 1:38 AM
 */
public class RpcException extends RuntimeException {

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RpcException(StatusEnum statusEnum) {
        super(statusEnum.getDescription());
    }

}
