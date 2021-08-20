package com.webbdong.rpc.server.boot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * RPC 服务端启动引导
 * @author Webb Dong
 * @date 2021-08-19 4:23 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Configuration
public class RpcServerBootstrap {

    private final RpcServerRunner runner;

    @PostConstruct
    public void initRpcServer() {
        runner.run();
    }

}
