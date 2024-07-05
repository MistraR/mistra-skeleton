package com.mistra.skeleton.redis.lock;

public interface RedLock extends DistributedLockFactory.Lock {

    String getKey();
}
