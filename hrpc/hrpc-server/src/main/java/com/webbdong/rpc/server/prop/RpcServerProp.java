package com.webbdong.rpc.server.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Webb Dong
 * @date 2021-08-19 3:12 PM
 */
@Data
@Component
public class RpcServerProp {

    /**
     * ZK根节点名称
     */
    @Value("${rpc.server.zk.root}")
    private String zkRoot;

    /**
     * ZK地址信息
     */
    @Value("${rpc.server.zk.addr}")
    private String zkAddr;


    /**
     * RPC通讯端口
     */
    @Value("${rpc.network.port}")
    private int rpcPort;

    /**
     * Spring Boot 服务端口
     */
    @Value("${server.port}")
    private int serverPort;

    /**
     * ZK连接超时时间配置
     */
    @Value("${rpc.server.zk.timeout:10000}")
    private int connectTimeout;

}
