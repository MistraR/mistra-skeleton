package com.mistra.cache.util;

import com.mistra.cache.config.Independent;
import com.mistra.cache.config.RedisErrorCode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/9/21 15:47
 * @ Description:
 */
@Component
public class ReentrantLockUtil {

    private static final Logger log = LoggerFactory.getLogger(ReentrantLockUtil.class);
    private static final int DEFAULT_WAIT_TIME = 2;
    private static final int DEFAULT_LEASE_TIME = 9;
    private final RedissonClient redissonClient;

    @Autowired
    public ReentrantLockUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public boolean tryLock(String lockKey, int waitTime, int leaseTime) {
        RLock lock = this.redissonClient.getLock(lockKey);

        try {
            return lock.tryLock((long) waitTime, (long) leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException var6) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public <T> T lockThenExecute(final String key, int waitTime, int leaseTime, Supplier<T> supplier) {
        if (!this.tryLock(key, waitTime, leaseTime)) {
            throw new RuntimeException(RedisErrorCode.OPERATE_TOO_FREQUENTLY.getMessage());
        } else if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            Object var5;
            try {
                var5 = supplier.get();
            } finally {
                this.unlock(key);
            }
            return (T) var5;
        } else {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCompletion(int status) {
                    ReentrantLockUtil.this.unlock(key);
                }
            });
            return supplier.get();
        }
    }

    public <T> T lockThenExecute(String key, Supplier<T> supplier) {
        return (T) this.lockThenExecute(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, (Supplier) supplier);
    }

    public void lockThenExecute(final String key, int waitTime, int leaseTime, Independent independent) {
        if (!this.tryLock(key, waitTime, leaseTime)) {
            throw new RuntimeException(RedisErrorCode.OPERATE_TOO_FREQUENTLY.getMessage());
        } else {
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                try {
                    independent.execute();
                } finally {
                    this.unlock(key);
                }
            } else {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        ReentrantLockUtil.this.unlock(key);
                    }
                });
                independent.execute();
            }

        }
    }

    public void lockThenExecute(String key, Independent independent) {
        this.lockThenExecute(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, independent);
    }

    public void unlock(String lockKey) {
        RLock lock = this.redissonClient.getLock(lockKey);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }
}
