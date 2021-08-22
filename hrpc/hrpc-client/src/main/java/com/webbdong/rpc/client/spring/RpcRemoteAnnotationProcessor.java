package com.webbdong.rpc.client.spring;

import com.webbdong.rpc.core.annotation.RpcRemote;
import com.webbdong.rpc.core.proxy.ProxyFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * RpcRemote 注解处理器，为 RpcRemote 注解修饰的字段创建代理对象并注入
 * @author Webb Dong
 * @date 2021-08-20 8:22 PM
 */
@Component
@Slf4j
public class RpcRemoteAnnotationProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ProxyFactory proxyFactory;

    @SneakyThrows
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            RpcRemote rpcRemote = field.getAnnotation(RpcRemote.class);
            if (rpcRemote != null) {
                // 创建代理对象
                Object proxy = proxyFactory.newProxyInstance(field.getType());
                log.info("为RpcRemote注解标注的属性生成的代理对象:{}", proxy);
                if (proxy != null) {
                    field.set(bean, proxy);
                }
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.proxyFactory = applicationContext.getBean(ProxyFactory.class);
    }

}
