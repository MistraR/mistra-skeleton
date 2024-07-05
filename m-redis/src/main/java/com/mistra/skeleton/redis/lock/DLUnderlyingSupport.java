package com.mistra.skeleton.redis.lock;

import org.redisson.api.RedissonClient;

public interface DLUnderlyingSupport {

    RedissonClient redisson();
}
