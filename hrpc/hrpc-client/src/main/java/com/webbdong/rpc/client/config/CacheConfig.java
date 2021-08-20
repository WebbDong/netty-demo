package com.webbdong.rpc.client.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.webbdong.rpc.core.model.ServiceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:36 PM
 */
@Configuration
public class CacheConfig {

    @Bean
    public LoadingCache<String, List<ServiceProvider>> buildCache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, List<ServiceProvider>>() {
                    @Override
                    public List<ServiceProvider> load(String key) throws Exception {
                        // 在这里可以初始化加载数据的缓存信息，读取数据库中信息或者是加载文件中的某些数据信息
                        return null;
                    }
                });
    }

}
