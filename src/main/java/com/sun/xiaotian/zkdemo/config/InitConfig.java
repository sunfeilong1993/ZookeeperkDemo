package com.sun.xiaotian.zkdemo.config;

import com.sun.xiaotian.zkdemo.constant.ZookeeperConstant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 初始化配置信息
 */

public class InitConfig {

    private static final Logger logger = LogManager.getLogger(InitConfig.class);

    private CountDownLatch connectDownLaunch = new CountDownLatch(1);
    private ZooKeeper zooKeeper;
    private final Random random = new Random(37);

    /**
     * 创建链接
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void connect() throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper(ZookeeperConstant.HOSTS, ZookeeperConstant.SESSION_TIME, new ConnectWatcher());
        connectDownLaunch.await();
    }

    /**
     * 初始化配置信息
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void init() throws KeeperException, InterruptedException {
        for (KeyConstant keyConstant : KeyConstant.values()) {
            Stat stat = zooKeeper.exists(ZookeeperConstant.DISTRIBUTE_CONFIG_PATH, false);
            if (stat == null) {
                zooKeeper.create(ZookeeperConstant.DISTRIBUTE_CONFIG_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String path = ZookeeperConstant.DISTRIBUTE_CONFIG_PATH + "/" + keyConstant.getText();
            stat = zooKeeper.exists(path, false);
            byte[] data = (random.nextInt() + "").getBytes();
            if (stat == null) {
                zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                zooKeeper.setData(path, data, -1);
            }
        }
        logger.info("配置信息初始化成功!");
    }

    public static void main(String[] args) {
        InitConfig initConfig = new InitConfig();
        try {
            initConfig.connect();
            initConfig.init();
        } catch (IOException e) {
            logger.error("创建连接失败!" + e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("zookeeper 连接中断!" + e.getMessage(), e);
        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        }
    }

    class ConnectWatcher implements Watcher {
        @Override
        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected) {
                connectDownLaunch.countDown();
            }
        }
    }
}
