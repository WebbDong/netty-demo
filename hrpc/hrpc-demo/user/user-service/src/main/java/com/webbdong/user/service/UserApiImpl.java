package com.webbdong.user.service;

import com.webbdong.rpc.core.annotation.RpcService;
import com.webbdong.user.api.UserApi;

/**
 * @author Webb Dong
 * @date 2021-08-19 10:08 PM
 */
@RpcService(interfaceClass = UserApi.class)
public class UserApiImpl implements UserApi {

    @Override
    public String getUser() {
        return "User(id=1, name=Ferrari)";
    }

}
