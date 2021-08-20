package com.webbdong.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * RPC 统一响应模型
 * @author Webb Dong
 * @date 2021-08-19 1:39 AM
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 响应数据
     */
    private Object result;

    /**
     * 异常
     */
    private Throwable cause;

    public boolean isError() {
        return cause != null;
    }

}
