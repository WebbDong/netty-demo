package com.webbdong.rpc.server.boot;

import com.webbdong.rpc.server.registry.RpcRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RPC 服务端启动
 * @author Webb Dong
 * @date 2021-08-19 4:27 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
public class RpcServerRunner {

    private final RpcRegistry registry;

    private final RpcServer rpcServer;

    public void run() {
        // 1、服务注册
        registry.serviceRegistry();
        // 2、基于 Netty 启动服务, 监听端口, 等待接收请求
        rpcServer.start();
    }

}
