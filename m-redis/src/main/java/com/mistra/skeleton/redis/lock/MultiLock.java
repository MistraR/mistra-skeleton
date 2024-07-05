package com.mistra.skeleton.redis.lock;

import java.util.Collection;

/**
 * 批量锁
 */
public interface MultiLock extends DistributedLockFactory.Lock {

    Collection<String> getKeys();

    int size();
}
