package com.webbdong.rpc.client.cluster;

import com.webbdong.rpc.client.prop.RpcClientProp;
import com.webbdong.rpc.core.annotation.RpcLoadBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Webb Dong
 * @date 2021-08-23 1:27 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
public class DefaultStrategyProvider implements StrategyProvider, ApplicationContextAware {

    private final RpcClientProp rpcClientProp;

    private LoadBalanceStrategy strategy;

    @Override
    public LoadBalanceStrategy getStrategy() {
        return strategy;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(RpcLoadBalance.class);
        String rpcClientClusterStrategy = rpcClientProp.getRpcClientClusterStrategy();
        if (rpcClientClusterStrategy == null) {
            rpcClientClusterStrategy = "random";
        }
        for (Object bean : beansMap.values()) {
            RpcLoadBalance rpcLoadBalance = bean.getClass().getAnnotation(RpcLoadBalance.class);
            if (rpcLoadBalance.strategy().equals(rpcClientClusterStrategy)) {
                strategy = (LoadBalanceStrategy) bean;
                break;
            }
        }
    }
}
