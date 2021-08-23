package com.webbdong.rpc.client.cluster.loadbalance;

import com.webbdong.rpc.client.cluster.LoadBalanceStrategy;
import com.webbdong.rpc.core.annotation.RpcLoadBalance;
import com.webbdong.rpc.core.model.ServiceProvider;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * 随机负载均衡算法
 * @author Webb Dong
 * @date 2021-08-23 10:51 AM
 */
@RpcLoadBalance(strategy = "random")
public class RandomLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {
        return serviceProviders.get(RandomUtils.nextInt(0, serviceProviders.size()));
    }

}
