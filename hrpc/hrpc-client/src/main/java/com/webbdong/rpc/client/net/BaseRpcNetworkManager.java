package com.webbdong.rpc.client.net;

import com.webbdong.rpc.core.cache.ServiceProviderCache;
import com.webbdong.rpc.core.enums.StatusEnum;
import com.webbdong.rpc.core.exception.RpcException;
import com.webbdong.rpc.core.model.RpcRequest;
import com.webbdong.rpc.core.model.RpcResponse;
import com.webbdong.rpc.core.model.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-21 1:12 PM
 */
@Slf4j
public abstract class BaseRpcNetworkManager implements RpcNetworkManager {

    private ServiceProviderCache serviceProviderCache;

    @Autowired
    public void setServiceProviderCache(ServiceProviderCache serviceProviderCache) {
        this.serviceProviderCache = serviceProviderCache;
    }

    public RpcResponse sendRequest(RpcRequest request) {
        List<ServiceProvider> serviceProviders = serviceProviderCache.get(request.getClassName());
        if (CollectionUtils.isEmpty(serviceProviders)) {
            log.info("客户端没有发现可用发服务节点");
            throw new RpcException(StatusEnum.NOT_FOUND_SERVICE_PROVIDER);
        }
        ServiceProvider serviceProvider = serviceProviders.get(0);
        if (serviceProvider == null) {
            throw new RpcException(StatusEnum.NOT_FOUND_SERVICE_PROVIDER);
        }
        return networkRequest(request, serviceProvider);
    }

    public abstract RpcResponse networkRequest(RpcRequest request, ServiceProvider serviceProvider);

}
