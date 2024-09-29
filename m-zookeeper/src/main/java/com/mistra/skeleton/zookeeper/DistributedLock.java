package com.mistra.skeleton.zookeeper;

import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 分布式锁工具
 * @ date: 2024/7/25
 */
@Component
public class DistributedLock {

    private CuratorFramework client;
    private InterProcessMutex lock;

    @Value("${zookeeper.connect-string}")
    private String zkConnectString;

    @Value("${zookeeper.session-timeout}")
    private int sessionTimeout;

    @Value("${zookeeper.connection-timeout}")
    private int connectionTimeout;

    @PostConstruct
    public void init() {
        client = CuratorFrameworkFactory.newClient(zkConnectString,
                sessionTimeout,
                connectionTimeout,
                new ExponentialBackoffRetry(1000, 3));
        client.start();
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            client.close();
        }
    }

    public void acquireLock(String lockPath) throws Exception {
        lock = new InterProcessMutex(client, lockPath);
        lock.acquire();
    }

    public void acquireLock(String lockPath, long time, TimeUnit unit) throws Exception {
        lock = new InterProcessMutex(client, lockPath);
        if (!lock.acquire(time, unit)) {
            throw new RuntimeException("Could not acquire the lock within the given time");
        }
    }

    public void releaseLock() throws Exception {
        if (lock != null && lock.isAcquiredInThisProcess()) {
            lock.release();
        }
    }
}
