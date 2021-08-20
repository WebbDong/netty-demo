package com.webbdong.rpc.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC 远程调用客户端注解
 * @author Webb Dong
 * @date 2021-08-19 1:21 AM
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcRemote {

    String value() default "";

    /**
     * 服务接口Class
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 服务接口名称
     * @return
     */
    String interfaceName() default "";

    /**
     * 服务版本号
     * @return
     */
    String version() default "";

    /**
     * 服务分组
     * @return
     */
    String group() default "";

}
