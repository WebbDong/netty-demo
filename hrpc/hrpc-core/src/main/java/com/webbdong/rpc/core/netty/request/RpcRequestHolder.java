package com.webbdong.rpc.core.netty.request;

import com.webbdong.rpc.core.model.ChannelMapping;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Webb Dong
 * @date 2021-08-19 1:01 PM
 */
@Component
@Slf4j
public class RpcRequestHolder {

    /**
     * 维护requestId与RequestPromise的映射关系
     */
    private static Map<String, RequestPromise> requestPromiseMap = new ConcurrentHashMap<>();

    /**
     * 维护客户端对所有服务节点的映射,达到重复利用已创建好的 channel key:serverip:port
     */
    private static Map<String, ChannelMapping> channelMappingMap = new ConcurrentHashMap<>();

    /**
     * 向容器中添加requestPromise
     * @param requestId
     * @param promise
     */
    public static void addRequestPromise(String requestId, RequestPromise promise) {
        requestPromiseMap.put(requestId, promise);
    }

    /**
     * 获取requestPromise
     * @param requestId
     * @return
     */
    public static RequestPromise getRequestPromise(String requestId) {
        return requestPromiseMap.get(requestId);
    }

    /**
     * 移除requestPromise
     * @param requestId
     */
    public static void removeRequestPromise(String requestId) {
        requestPromiseMap.remove(requestId);
    }

    /**
     * 判断客户端是否已存在该服务节点的连接
     * @param serverIp
     * @param serverPort
     * @return
     */
    public static boolean channelExist(String serverIp, int serverPort) {
        return getChannel(serverIp, serverPort) != null;
    }

    /**
     * 添加客户端Channel映射
     */
    public static void addChannelMapping(ChannelMapping channelMapping) {
        channelMappingMap.put(channelMapping.getIpWithPort(), channelMapping);
    }

    /**
     * 获取客户端Channel
     * @param serverIp
     * @param serverPort
     * @return
     */
    public static Channel getChannel(String serverIp, int serverPort) {
        String ipWithPort = serverIp + ":" + serverPort;
        ChannelMapping channelMapping = channelMappingMap.get(ipWithPort);
        if (channelMapping != null) {
            return channelMapping.getChannel();
        } else {
            return null;
        }
    }

    /**
     * 移除并关闭Channel
     * @param serverIp
     * @param serverPort
     */
    public static void removeChannel(String serverIp, int serverPort) {
        String ipWithPort = serverIp + ":" + serverPort;
        ChannelMapping channelMapping = channelMappingMap.get(ipWithPort);
        if (channelMapping != null) {
            channelMapping.getChannel().closeFuture();
            channelMapping.getChannel().eventLoop().shutdownGracefully();
            log.info("channel已关闭,移除映射");
            channelMappingMap.remove(ipWithPort);
        }
    }

    @PreDestroy
    private void destroyAllChannel() {
        Collection<ChannelMapping> values = channelMappingMap.values();
        for (ChannelMapping channelMapping : values) {
            channelMapping.getChannel().closeFuture();
            channelMapping.getChannel().eventLoop().shutdownGracefully();
            String ipWithPort = channelMapping.getIp() + ":" + channelMapping.getPort();
            channelMappingMap.remove(ipWithPort);
        }
    }

}
