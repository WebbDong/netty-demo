package com.webbdong.rpc.client.cluster.loadbalance;

import com.webbdong.rpc.client.cluster.LoadBalanceStrategy;
import com.webbdong.rpc.core.annotation.RpcLoadBalance;
import com.webbdong.rpc.core.model.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮询负载均衡算法
 * @author Webb Dong
 * @date 2021-08-23 10:57 AM
 */
@RpcLoadBalance(strategy = "polling")
@Slf4j
public class PollingLoadBalanceStrategy implements LoadBalanceStrategy {

    private int index;

    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {
        try {
            lock.tryLock(10, TimeUnit.SECONDS);
            if (index >= serviceProviders.size()) {
                index = 0;
            }
            return serviceProviders.get(index++);
        } catch (InterruptedException e) {
            log.error("轮询策略获取锁失败,msg= {}", e);
        } finally {
            lock.unlock();
        }
        return null;
    }

}
