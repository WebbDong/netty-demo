package com.webbdong.rpc.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 服务提供方模型
 * @author Webb Dong
 * @date 2021-08-19 1:44 AM
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider {

    private String serviceName;

    private String serverIp;

    private int rpcPort;

    private int networkPort;

    private long timeout;

    private int weight;

}
