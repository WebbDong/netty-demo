package com.webbdong.rpc.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC 负载均衡注解
 * @author Webb Dong
 * @date 2021-08-19 12:59 AM
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcLoadBalance {

    /**
     * 负载均衡策略
     */
    String strategy() default "random";

}
