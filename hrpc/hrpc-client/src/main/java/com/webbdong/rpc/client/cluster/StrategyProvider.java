package com.webbdong.rpc.client.cluster;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:32 PM
 */
public interface StrategyProvider {

    LoadBalanceStrategy getStrategy();

}
