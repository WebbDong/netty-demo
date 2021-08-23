package com.webbdong.rpc.core.model;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Webb Dong
 * @date 2021-08-19 12:59 PM
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelMapping {

    /**
     * 服务提供者节点，格式: ip:port
     */
    private String ipWithPort;

    /**
     * 服务提供者ip
     */
    private String ip;

    /**
     * 服务提供者端口
     */
    private int port;

    /**
     * 客户端连接通道channel
     */
    private Channel channel;

    public String getIpWithPort() {
        return ip + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChannelMapping that = (ChannelMapping) o;
        return port == that.port && ip.equals(that.ip);
    }

}
