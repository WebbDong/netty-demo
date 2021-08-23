package com.webbdong.rpc.client.cluster.loadbalance;

import com.webbdong.rpc.client.cluster.LoadBalanceStrategy;
import com.webbdong.rpc.core.annotation.RpcLoadBalance;
import com.webbdong.rpc.core.model.ServiceProvider;
import com.webbdong.rpc.core.util.IpUtil;

import java.util.List;

/**
 * 哈希负载均衡算法
 * @author Webb Dong
 * @date 2021-08-23 10:46 AM
 */
@RpcLoadBalance(strategy = "hash")
public class HashLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {
        int hashCode = IpUtil.getRealIp().hashCode();
        return serviceProviders.get(Math.abs(hashCode % serviceProviders.size()));
    }

}
