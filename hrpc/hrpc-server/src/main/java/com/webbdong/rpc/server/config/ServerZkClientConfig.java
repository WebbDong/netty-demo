package com.webbdong.rpc.server.config;

import com.webbdong.rpc.server.prop.RpcServerProp;
import lombok.RequiredArgsConstructor;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Webb Dong
 * @date 2021-08-19 3:37 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Configuration
public class ServerZkClientConfig {

    private final RpcServerProp rpcServerProp;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(rpcServerProp.getZkAddr(), rpcServerProp.getConnectTimeout());
    }

}
