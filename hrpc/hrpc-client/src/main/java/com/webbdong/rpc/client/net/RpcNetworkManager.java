package com.webbdong.rpc.client.net;

import com.webbdong.rpc.core.model.RpcRequest;
import com.webbdong.rpc.core.model.RpcResponse;

/**
 * @author Webb Dong
 * @date 2021-08-21 11:04 AM
 */
public interface RpcNetworkManager {

    RpcResponse sendRequest(RpcRequest request);

}
