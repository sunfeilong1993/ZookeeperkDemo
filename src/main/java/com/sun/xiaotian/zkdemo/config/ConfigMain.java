package com.sun.xiaotian.zkdemo.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * 配置文件使用启动类
 */

public class ConfigMain {

    private final static Logger logger = LogManager.getLogger(ConfigMain.class);

    public static void main(String[] args) throws InterruptedException {
        ConfigMain configMain = new ConfigMain();
        configMain.start();
        TimeUnit.SECONDS.sleep(1000);
    }

    public void start() {
        //初始化配置信息
        try {
            initConfigInfo();
        } catch (Exception e) {
            logger.error("初始化配置信息失败!", e);
        }

        //客户端获取配置信息
        DistributeClientA distributeClientA = new DistributeClientA();
        DistributeClientB distributeClientB = new DistributeClientB();
        try {
            distributeClientA.start();
            distributeClientB.start();
        } catch (IOException e) {
            logger.error("创建连接失败!" + e.getMessage(), e);
            return;
        } catch (InterruptedException e) {
            logger.error("zookeeper 连接中断!" + e.getMessage(), e);
            return;
        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        //定时更新配置文件
        updateConfig();
    }

    //初始化配置文件
    public void initConfigInfo() throws ExecutionException, InterruptedException {
        InitConfigCallable initConfigCallable = new InitConfigCallable();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> submit = executorService.submit(initConfigCallable);
        //等待初始化执行完成
        submit.get();
    }

    //更新配置信息
    public void updateConfig() {
        UpdateConfigThread updateThread = new UpdateConfigThread();
        updateThread.setDaemon(true);
        updateThread.start();
    }
}
