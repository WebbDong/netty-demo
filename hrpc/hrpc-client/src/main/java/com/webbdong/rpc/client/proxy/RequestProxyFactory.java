package com.webbdong.rpc.client.proxy;

import com.webbdong.rpc.client.net.RpcNetworkManager;
import com.webbdong.rpc.core.proxy.ProxyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:57 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
@Slf4j
public class RequestProxyFactory implements ProxyFactory {

    private final RpcNetworkManager rpcNetworkManager;

    /**
     * 创建新的代理实例-CGLib动态代理
     * @param cls
     * @param <T>
     * @return
     */
    @Override
    public  <T> T newProxyInstance(Class<T> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new CglibProxyCallBackHandler(rpcNetworkManager));
        return (T) enhancer.create();
    }

}
