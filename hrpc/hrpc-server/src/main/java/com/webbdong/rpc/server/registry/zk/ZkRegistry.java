package com.webbdong.rpc.server.registry.zk;

import com.webbdong.rpc.core.annotation.RpcService;
import com.webbdong.rpc.core.spring.SpringBeanFactory;
import com.webbdong.rpc.core.util.IpUtil;
import com.webbdong.rpc.server.prop.RpcServerProp;
import com.webbdong.rpc.server.registry.RpcRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ZK 注册实现
 * @author Webb Dong
 * @date 2021-08-19 4:33 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
@Slf4j
public class ZkRegistry implements RpcRegistry {

    private final SpringBeanFactory springBeanFactory;

    private final ServerZkKit serverZkKit;

    private final RpcServerProp prop;

    @Override
    public void serviceRegistry() {
        // 1、找到所有标有注解 RpcService 的类, 将服务信息写入到 zk 中
        Map<String, Object> annotationClass = springBeanFactory.getBeanListByAnnotationClass(RpcService.class);
        if (annotationClass != null && annotationClass.size() != 0) {
            // 2、创建服务根节点
            serverZkKit.createRootNode();
            String ip = IpUtil.getRealIp();
            for (Object bean : annotationClass.values()) {
                // 3、获取 bean 上的 RpcService 注解
                RpcService annotation = bean.getClass().getAnnotation(RpcService.class);
                // 4、获取注解的属性 interfaceClass
                Class<?> interfaceClass = annotation.interfaceClass();
                // 5、创建接口
                String serviceName = interfaceClass.getName();
                serverZkKit.createPersistentNode(serviceName);
                // 6、在该接口下创建临时节点信息 ip:rpcport
                String node = ip + ":" + prop.getRpcPort();
                serverZkKit.createNode(serviceName + "/" + node);
                log.info("服务{}-{}注册成功", serviceName, node);
            }
        }
    }

}
