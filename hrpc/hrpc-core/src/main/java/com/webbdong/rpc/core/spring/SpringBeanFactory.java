package com.webbdong.rpc.core.spring;

import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Webb Dong
 * @date 2021-08-19 11:22 AM
 */
@Component
public class SpringBeanFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据Class获取bean
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }

    /**
     * 根据beanName获取bean
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /***
     * 获取有指定注解的对象
     * @param annotationClass
     * @return
     */
    public static Map<String, Object> getBeanListByAnnotationClass(Class<? extends Annotation> annotationClass) {
        return applicationContext.getBeansWithAnnotation(annotationClass);
    }

    /**
     * 向容器注册单例bean
     * @param bean
     */
    public static void registerSingleton(Object bean) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        // 让bean完成Spring初始化过程中所有增强器检验，只是不重新创建bean
        beanFactory.applyBeanPostProcessorsAfterInitialization(bean, bean.getClass().getName());
        // 将bean以单例的形式入驻到容器中，此时通过bean.getClass().getName()或bean.getClass()都可以拿到放入Spring容器的Bean
        beanFactory.registerSingleton(bean.getClass().getName(), bean);
    }

}
