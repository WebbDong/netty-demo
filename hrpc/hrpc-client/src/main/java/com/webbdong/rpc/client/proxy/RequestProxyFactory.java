package com.webbdong.rpc.client.proxy;

import com.webbdong.rpc.core.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:57 PM
 */
@Component
@Slf4j
public class RequestProxyFactory implements ProxyFactory {

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
        enhancer.setCallback(new CglibProxyCallBackHandler());
        return (T) enhancer.create();
    }

}
