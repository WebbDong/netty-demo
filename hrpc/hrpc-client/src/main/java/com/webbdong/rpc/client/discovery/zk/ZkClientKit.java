package com.webbdong.rpc.client.discovery.zk;

import com.google.common.collect.Lists;
import com.webbdong.rpc.client.prop.RpcClientProp;
import com.webbdong.rpc.core.cache.ServiceProviderCache;
import com.webbdong.rpc.core.model.ServiceProvider;
import lombok.RequiredArgsConstructor;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Webb Dong
 * @date 2021-08-19 2:48 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
public class ZkClientKit {

    private final RpcClientProp prop;

    private final ZkClient zkClient;

    private final ServiceProviderCache cache;

    /**
     * 服务订阅接口
     * @param serviceName
     */
    public void subscribeZKEvent(String serviceName) {
        // 1. 组装服务节点信息
        String path = prop.getZkRoot() + "/" + serviceName;
        // 2. 订阅服务节点（监听节点变化）
        zkClient.subscribeChildChanges(path, (parentPath, list) -> {
            // 3. 判断获取的节点信息，是否为空
            if (CollectionUtils.isNotEmpty(list)) {
                // 4. 将服务端获取的信息， 转换为服务记录对象
                List<ServiceProvider> providerServices = convertToProviderService(serviceName, list);
                // 5. 更新缓存信息 记得要改
                cache.update(serviceName, providerServices);
            }
        });
    }


    /**
     * 获取所有服务列表：所有的服务接口信息
     * @return
     */
    public List<String> getServiceList() {
        String path = prop.getZkRoot();
        List<String> children = zkClient.getChildren(path);
        return children;
    }

    /**
     *  根据服务名称获取服务节点完整信息
     * @param serviceName
     * @return
     */
    public List<ServiceProvider> getServiceInfos(String serviceName) {
        String path = prop.getZkRoot() + "/" + serviceName;
        List<String> children = zkClient.getChildren(path);
        List<ServiceProvider> providerServices = convertToProviderService(serviceName, children);
        return providerServices;
    }

    /**
     * 将拉取的服务节点信息转换为服务记录对象
     *
     * @param serviceName
     * @param list
     * @return
     */
    private List<ServiceProvider> convertToProviderService(String serviceName, List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayListWithCapacity(0);
        }
        // 将服务节点信息转换为服务记录对象
        List<ServiceProvider> providerServices = list.stream().map(v -> {
            String[] serviceInfos = v.split(":");
            return ServiceProvider.builder()
                    .serviceName(serviceName)
                    .serverIp(serviceInfos[0])
                    .rpcPort(Integer.parseInt(serviceInfos[1]))
                    .build();
        }).collect(Collectors.toList());
        return providerServices;
    }

}
