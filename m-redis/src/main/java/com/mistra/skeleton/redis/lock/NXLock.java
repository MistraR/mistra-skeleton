package com.mistra.skeleton.redis.lock;

public interface NXLock extends DistributedLockFactory.Lock {

    String getKey();
}
