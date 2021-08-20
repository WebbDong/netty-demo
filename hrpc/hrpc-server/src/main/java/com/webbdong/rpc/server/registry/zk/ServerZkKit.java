package com.webbdong.rpc.server.registry.zk;

import com.webbdong.rpc.server.prop.RpcServerProp;
import lombok.RequiredArgsConstructor;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Webb Dong
 * @date 2021-08-19 3:45 PM
 */
@RequiredArgsConstructor(onConstructor_={@Autowired})
@Component
public class ServerZkKit {

    private final ZkClient zkClient;

    private final RpcServerProp prop;

    /***
     * 根节点创建
     */
    public void createRootNode() {
        boolean exists = zkClient.exists(prop.getZkRoot());
        if (!exists) {
            zkClient.createPersistent(prop.getZkRoot());
        }
    }

    /***
     * 创建其他节点
     * @param path
     */
    public void createPersistentNode(String path) {
        String pathName = prop.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createPersistent(pathName);
        }
    }

    /***
     * 创建节点
     * @param path
     */
    public void createNode(String path) {
        String pathName = prop.getZkRoot() + "/" + path;
        boolean exists = zkClient.exists(pathName);
        if (!exists) {
            zkClient.createEphemeral(pathName);
        }
    }

}
