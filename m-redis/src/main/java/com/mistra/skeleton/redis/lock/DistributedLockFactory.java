package com.mistra.skeleton.redis.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工厂接口
 */
public interface DistributedLockFactory {

    NXLock newNXLock(String key);

    @Deprecated
    RedLock newRedLock(String... keys);

    MultiLock newMultiLock(String... keys);

    <T> T executeWithTryLockThrows(String key, int waitTime, int leaseTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable;

    <T> T executeWithLockThrows(String key, int leaseTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable;

    /**
     * 默认锁接口
     */
    interface Lock {

        void lock() throws LockException;

        boolean tryLock(long milliseconds) throws LockException;

        boolean tryLock(TimeUnit unit, long period) throws LockException;

        void unlock();
    }

    @FunctionalInterface
    interface SupplierThrow<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }
}
