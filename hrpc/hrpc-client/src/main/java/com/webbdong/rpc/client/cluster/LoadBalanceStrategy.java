package com.webbdong.rpc.client.cluster;

import com.webbdong.rpc.core.model.ServiceProvider;

import java.util.List;

/**
 * 负载均衡算法策略接口
 * @author Webb Dong
 * @date 2021-08-19 2:32 PM
 */
public interface LoadBalanceStrategy {

    /**
     * 根据对应的策略进行负载均衡
     * @param serviceProviders
     * @return
     */
    ServiceProvider select(List<ServiceProvider> serviceProviders);

}
