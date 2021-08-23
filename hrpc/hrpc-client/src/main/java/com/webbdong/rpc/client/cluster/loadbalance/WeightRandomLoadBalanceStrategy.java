package com.webbdong.rpc.client.cluster.loadbalance;

import com.webbdong.rpc.client.cluster.LoadBalanceStrategy;
import com.webbdong.rpc.core.annotation.RpcLoadBalance;
import com.webbdong.rpc.core.model.ServiceProvider;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 加权随机负载均衡算法
 * @author Webb Dong
 * @date 2021-08-23 11:56 AM
 */
@RpcLoadBalance(strategy = "weightRandom")
public class WeightRandomLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {
        // 按照权重创建一个新的待选集合，在新的集合中随机选择
        List<ServiceProvider> newList = new ArrayList<>();
        for (ServiceProvider serviceProvider : serviceProviders) {
            for (int i = 0; i < serviceProvider.getWeight(); i++) {
                // 根据权重向新集合中添加节点
                newList.add(serviceProvider);
            }
        }
        return newList.get(RandomUtils.nextInt(0, newList.size()));
    }

}
