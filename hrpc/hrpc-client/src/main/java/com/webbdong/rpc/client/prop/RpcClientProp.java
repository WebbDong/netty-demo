package com.webbdong.rpc.client.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:36 PM
 */
@Data
@Component
public class RpcClientProp {

    @Value("${rpc.client.zk.root}")
    private String zkRoot;

    @Value("${rpc.client.zk.addr}")
    private String zkAddr;

    @Value("${server.port}")
    private String znsClientPort;

    @Value("${rpc.cluster.strategy}")
    private String rpcClientClusterStrategy;

    @Value("${rpc.client.zk.timeout}")
    private Integer connectTimeout;

}
