package com.webbdong.rpc.client.discovery.zk;

import com.webbdong.rpc.client.discovery.RpcServiceDiscovery;
import com.webbdong.rpc.core.cache.ServiceProviderCache;
import com.webbdong.rpc.core.model.ServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 ZK 的服务发现实现
 * @author Webb Dong
 * @date 2021-08-20 2:16 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
@Slf4j
public class ZkServiceDiscovery implements RpcServiceDiscovery {

    private final ZkClientKit kit;

    private final ServiceProviderCache cache;

    @Override
    public void serviceDiscovery() {
        // 1、拉取所有服务接口列表
        List<String> serviceList = kit.getServiceList();
        if (serviceList != null && !serviceList.isEmpty()) {
            for (String serviceName : serviceList) {
                // 2、获取该接口下的节点列表
                List<ServiceProvider> providers = kit.getServiceInfos(serviceName);
                // 3、缓存该服务的所有节点信息
                log.info("订阅的服务名为={},服务提供者有= {}", serviceName, providers);
                cache.put(serviceName, providers);
                // 4、监听该服务下所有节点的信息变化
                kit.subscribeZKEvent(serviceName);
            }
        }
    }

}
