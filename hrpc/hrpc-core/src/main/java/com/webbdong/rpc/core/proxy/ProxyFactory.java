package com.webbdong.rpc.core.proxy;

/**
 * @author Webb Dong
 * @date 2021-08-19 11:15 AM
 */
public interface ProxyFactory {

    <T> T newProxyInstance(Class<T> cls);

}
