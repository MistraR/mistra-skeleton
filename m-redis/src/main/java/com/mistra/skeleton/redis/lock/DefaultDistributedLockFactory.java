package com.mistra.skeleton.redis.lock;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import lombok.extern.slf4j.Slf4j;

public final class DefaultDistributedLockFactory implements DistributedLockFactory {

    private final DLUnderlyingSupport dlUnderlyingSupport;
    private final DLEnvironment dlEnvironment;

    public DefaultDistributedLockFactory(DLEnvironment dlEnvironment) {
        this.dlUnderlyingSupport = dlEnvironment.underlying();
        this.dlEnvironment = dlEnvironment;
    }

    @Override
    public NXLock newNXLock(String key) {
        return new NXLockImpl(key);
    }

    @Override
    @Deprecated
    public RedLock newRedLock(String... keys) {
        if (!dlEnvironment.redLockSupported()) {
            throw new RuntimeException("The current redis config does not support red lock");
        }
        return new RedLockImpl(keys);
    }

    @Override
    public MultiLock newMultiLock(String... keys) {
        return new MultiLockImpl(keys);
    }

    @Override
    public <T> T executeWithTryLockThrows(String key, int waitTime, int leaseTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = dlUnderlyingSupport.redisson().getLock(key);
        boolean lockSuccess = lock.tryLock(waitTime, leaseTime, unit);
        if (!lockSuccess) {
            throw new RuntimeException(LockErrors.OPERATE_TOO_FREQUENTLY.description());
        }
        try {
            //执行锁内的代码逻辑
            return supplier.get();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public <T> T executeWithLockThrows(String key, int leaseTime, TimeUnit unit, SupplierThrow<T> supplier) throws Throwable {
        RLock lock = dlUnderlyingSupport.redisson().getLock(key);
        lock.lock(leaseTime, unit);
        try {
            //执行锁内的代码逻辑
            return supplier.get();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Slf4j
    private static abstract class AbstractLockImpl implements Lock {

        protected final String[] keys;
        private final RLock rLock;

        protected AbstractLockImpl(String... keys) {
            this.keys = keys;
            this.rLock = createLock(keys);
        }

        protected abstract RLock createLock(String... keys);

        @Override
        public void lock() throws LockException {
            try {
                rLock.lock();
            } catch (IllegalStateException e) {
                log.error("lock acquired failed", e);
            } catch (Exception e) {
                log.error("lock acquired failed", e);
                throw new LockException(e);
            }
        }

        @Override
        public boolean tryLock(long milliseconds) throws LockException {
            return tryLock(TimeUnit.MILLISECONDS, milliseconds);
        }

        @Override
        public boolean tryLock(TimeUnit unit, long period) throws LockException {
            try {
                return rLock.tryLock(period, unit);
            } catch (IllegalStateException e) {
                log.error("nx lock acquired failed", e);
                return false;
            } catch (Exception e) {
                log.error("nx lock acquired failed", e);
                throw new LockException(e);
            }
        }

        @Override
        public void unlock() {
            rLock.unlock();
        }
    }

    /**
     * 基于redis的NX锁
     */
    private class NXLockImpl extends AbstractLockImpl implements NXLock {

        private NXLockImpl(String key) {
            super(key);
        }

        @Override
        public String getKey() {
            return keys[0];
        }

        @Override
        protected RLock createLock(String... keys) {
            final RedissonClient redisson = dlUnderlyingSupport.redisson();
            return redisson.getLock(keys[0]);
        }
    }

    /**
     * 基于redis的红锁
     */
    @Deprecated
    private class RedLockImpl extends AbstractLockImpl implements RedLock {

        private RedLockImpl(String... keys) {
            super(keys);
        }

        @Override
        protected RLock createLock(String... keys) {
            final RedissonClient redisson = dlUnderlyingSupport.redisson();
            RLock[] locks = new RLock[keys.length];
            for (int i = 0; i < keys.length; i++) {
                locks[i] = redisson.getLock(keys[i]);
            }
            return redisson.getRedLock(locks);
        }

        @Override
        public String getKey() {
            return keys[0];
        }
    }

    /**
     * 基于redis的批量锁
     */
    private class MultiLockImpl extends AbstractLockImpl implements MultiLock {

        private MultiLockImpl(String... keys) {
            super(keys);
        }

        @Override
        protected RLock createLock(String... keys) {
            final RedissonClient redisson = dlUnderlyingSupport.redisson();
            RLock[] locks = new RLock[keys.length];
            for (int i = 0; i < keys.length; i++) {
                locks[i] = redisson.getLock(keys[i]);
            }
            return redisson.getMultiLock(locks);
        }

        @Override
        public Collection<String> getKeys() {
            return Arrays.asList(keys);
        }

        @Override
        public int size() {
            return keys.length;
        }
    }
}
