package com.webbdong.rpc.client.route;

import com.webbdong.rpc.core.model.ServiceProvider;

import java.util.List;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:59 PM
 */
public interface ServiceRoute {

    List<ServiceProvider> route(List<ServiceProvider> serviceProviderList);

}
