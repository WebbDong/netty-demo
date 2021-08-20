package com.webbdong.rpc.client.cache.local;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.webbdong.rpc.core.cache.ServiceProviderCache;
import com.webbdong.rpc.core.model.ServiceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:23 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
public class DefaultServiceProviderCache implements ServiceProviderCache {

    private final LoadingCache<String, List<ServiceProvider>> cache;

    @Override
    public void put(String key, List<ServiceProvider> value) {
        cache.put(key,value);
    }

    @Override
    public List<ServiceProvider> get(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            return Lists.newArrayListWithCapacity(0);
        }
    }

    @Override
    public void evict(String key) {
        cache.invalidate(key);
    }

    @Override
    public void update(String key, List<ServiceProvider> value) {
        evict(key);
        put(key,value);
    }

}
