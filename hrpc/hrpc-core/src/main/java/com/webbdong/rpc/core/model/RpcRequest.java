package com.webbdong.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * RPC 统一请求模型
 * @author Webb Dong
 * @date 2021-08-19 1:39 AM
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 远程目标类名
     */
    private String className;

    /**
     * 远程目标方法名
     */
    private String methodName;

    /**
     * 方法参数类型数组
     */
    private Class<?>[] parameterTypes;

    /**
     * 方法参数数组
     */
    private Object[] parameters;

}
