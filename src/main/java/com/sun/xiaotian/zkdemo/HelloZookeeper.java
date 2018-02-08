package com.sun.xiaotian.zkdemo;

import org.I0Itec.zkclient.ZkClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;

/**
 * 第一个程序
 */
public class HelloZookeeper {

    private static final Logger logger = LogManager.getLogger(HelloZookeeper.class);

    public static void main(String[] args) {
        ZkClient zkClient = null;
        try {
            zkClient = new ZkClient("43.225.158.197", 3000);
            zkClient.create("/Test", "HelloZookeeper", CreateMode.EPHEMERAL);
            logger.info("创建节点成功! " + "/Test/HelloZookeeper");
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }
}
