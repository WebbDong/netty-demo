package com.webbdong.rpc.core.netty.handler;

import com.webbdong.rpc.core.model.RpcRequest;
import com.webbdong.rpc.core.model.RpcResponse;
import com.webbdong.rpc.core.spring.SpringBeanFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * RPC 请求处理器
 * @author Webb Dong
 * @date 2021-08-20 12:35 AM
 */
@Component
@RequiredArgsConstructor(onConstructor_={@Autowired})
@ChannelHandler.Sharable
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final SpringBeanFactory springBeanFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        log.info("rpc服务端接收到的请求为:{}", request);
        RpcResponse response = new RpcResponse();
        try {
            response.setRequestId(request.getRequestId());
            // 目标对象与目标方法
            Object targetBean = springBeanFactory.getBean(Class.forName(request.getClassName()));
            Method targetMethod = targetBean.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            // 调用目标方法
            Object result = targetMethod.invoke(targetBean, request.getParameters());
            response.setResult(result);
        } catch (Throwable e) {
            response.setCause(e);
            log.error("rpc server invoke error", e);
        } finally {
            log.info("服务端响应为:{}", response);
            ctx.channel().writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端出现异常", cause);
        super.exceptionCaught(ctx, cause);
    }

}
