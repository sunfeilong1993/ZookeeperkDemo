package com.sun.xiaotian.zkdemo.config;

import com.sun.xiaotian.zkdemo.constant.ZookeeperConstant;
import com.sun.xiaotian.zkdemo.factory.ZookeeperFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;

/**
 * 初始化配置信息
 */

public class InitConfig {

    private static final Logger logger = LogManager.getLogger(InitConfig.class);

    private ZooKeeper zooKeeper;
    private final Random random = new Random(37);

    /**
     * 初始化配置信息
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void init() throws KeeperException, InterruptedException, IOException {
        zooKeeper = ZookeeperFactory.getZookeeper();
        for (KeyConstant keyConstant : KeyConstant.values()) {
            Stat stat = zooKeeper.exists(ZookeeperConstant.DISTRIBUTE_CONFIG_PATH, false);
            if (stat == null) {
                zooKeeper.create(ZookeeperConstant.DISTRIBUTE_CONFIG_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String path = ZookeeperConstant.DISTRIBUTE_CONFIG_PATH + "/" + keyConstant.getText();
            stat = zooKeeper.exists(path, false);
            byte[] data = (random.nextInt(1000) + "").getBytes(ZookeeperConstant.UTF_8);
            if (stat == null) {
                zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                zooKeeper.setData(path, data, -1);
            }
        }
        logger.info("配置信息初始化成功!");
    }
}
