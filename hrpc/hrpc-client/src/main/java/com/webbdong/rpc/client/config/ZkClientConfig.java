package com.webbdong.rpc.client.config;

import com.webbdong.rpc.client.prop.RpcClientProp;
import lombok.RequiredArgsConstructor;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:39 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Configuration
public class ZkClientConfig {

    private static final int EXPIRE_SECONDS = 86400;

    private final RpcClientProp prop;

    @Bean
    public ZkClient zkClient() {
        return new ZkClient(prop.getZkAddr(), prop.getConnectTimeout());
    }

}
