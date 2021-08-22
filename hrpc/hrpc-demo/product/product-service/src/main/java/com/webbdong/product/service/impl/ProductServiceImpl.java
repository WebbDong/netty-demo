package com.webbdong.product.service.impl;

import com.webbdong.product.service.ProductService;
import com.webbdong.rpc.core.annotation.RpcRemote;
import com.webbdong.user.api.UserApi;
import org.springframework.stereotype.Service;

/**
 * @author Webb Dong
 * @date 2021-08-21 8:00 PM
 */
@Service
public class ProductServiceImpl implements ProductService {

    @RpcRemote
    private UserApi userApi;

    @Override
    public String getProduct() {
        return "getProduct(), user = " + userApi.getUser();
    }

}
