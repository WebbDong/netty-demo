package com.webbdong.rpc.client.boot;

import com.webbdong.rpc.client.discovery.RpcServiceDiscovery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RPC 客户端启动
 * @author Webb Dong
 * @date 2021-08-20 12:01 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
public class RpcClientRunner {

    private final RpcServiceDiscovery discovery;

    public void run() {
        // 1、开启服务发现
        discovery.serviceDiscovery();
    }

}
