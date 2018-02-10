package com.sun.xiaotian.zkdemo.config;

import com.sun.xiaotian.zkdemo.constant.ZookeeperConstant;
import com.sun.xiaotian.zkdemo.factory.ZookeeperFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * 分布式客户端
 */

public abstract class DistributeClient {

    private final static Logger logger = LogManager.getLogger(DistributeClient.class);

    /**
     * 运行客户端
     *
     * @throws InterruptedException
     * @throws IOException
     * @throws KeeperException
     */
    public void start() throws InterruptedException, IOException, KeeperException {
        getConfigInfo();
    }

    private ZooKeeper getZookeeper() throws IOException, InterruptedException {
        return ZookeeperFactory.getZookeeper();
    }

    /**
     * 获取配置信息
     */
    private void getConfigInfo() throws IOException, InterruptedException, KeeperException {
        ZooKeeper zookeeper = getZookeeper();
        String parentPath = ZookeeperConstant.DISTRIBUTE_CONFIG_PATH;

        for (KeyConstant keyConstant : KeyConstant.values()) {
            String path = parentPath + "/" + keyConstant.getText();
            Stat nodeStat = zookeeper.exists(path, false);
            if (nodeStat == null) {
                logger.error("配置信息节点不存在, 不存在节点：" + path);
                throw new RuntimeException("");
            } else {
                byte[] data = zookeeper.getData(path, (watchedEvent) -> {
                    updateConfigInfo();
                }, null);
                logger.info(this.getClass().getSimpleName() + "配置信息, path: " + path + " value: " + new String(data, ZookeeperConstant.UTF_8));
            }
        }
    }

    private void updateConfigInfo() {
        try {
            getConfigInfo();
        } catch (Exception e) {
            logger.error("获取罪行配置信息失败!", e);
        }
    }
}
