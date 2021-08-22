package com.webbdong.rpc.client.boot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * RPC 客户端启动引导
 * @author Webb Dong
 * @date 2021-08-20 11:24 AM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Configuration
public class RpcClientBootstrap {

    private final RpcClientRunner runner;

    @PostConstruct
    public void initRpcClient() {
        runner.run();
    }

}
