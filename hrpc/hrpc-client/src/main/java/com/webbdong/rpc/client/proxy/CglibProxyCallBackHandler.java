package com.webbdong.rpc.client.proxy;

import com.webbdong.rpc.client.net.RpcNetworkManager;
import com.webbdong.rpc.core.model.RpcRequest;
import com.webbdong.rpc.core.model.RpcResponse;
import com.webbdong.rpc.core.util.RequestIdUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 代理处理器，实现 RPC 网络请求
 * @author Webb Dong
 * @date 2021-08-19 2:57 PM
 */
public class CglibProxyCallBackHandler implements MethodInterceptor {

    private RpcNetworkManager rpcNetworkManager;

    public CglibProxyCallBackHandler(RpcNetworkManager rpcNetworkManager) {
        this.rpcNetworkManager = rpcNetworkManager;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // 放行 Object 的 toString、hashCode、equals 等方法
        if (ReflectionUtils.isObjectMethod(method)) {
            return method.invoke(method.getDeclaringClass().newInstance(), args);
        }
        // 发送 RPC 请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(RequestIdUtil.requestId())
                .className(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        RpcResponse response = rpcNetworkManager.sendRequest(rpcRequest);
        return response.getResult();
    }

}
