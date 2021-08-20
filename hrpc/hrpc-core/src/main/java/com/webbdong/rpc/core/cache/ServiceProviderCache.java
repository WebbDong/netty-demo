package com.webbdong.rpc.core.cache;

import com.webbdong.rpc.core.model.ServiceProvider;

import java.util.List;

/**
 * 服务提供者缓存接口
 * @author Webb Dong
 * @date 2021-08-19 11:11 AM
 */
public interface ServiceProviderCache {

    /**
     * 向缓存中添加数据
     * @param key
     * @param value
     */
    void put(String key, List<ServiceProvider> value);

    /**
     * 获取缓存
     * @param key
     * @return
     */
    List<ServiceProvider> get(String key);

    /**
     * 缓存清除
     * @param key
     */
    void evict(String key);


    /**
     * 缓存更新
     * @param key
     * @param value
     */
    void update(String key,List<ServiceProvider> value);

}
