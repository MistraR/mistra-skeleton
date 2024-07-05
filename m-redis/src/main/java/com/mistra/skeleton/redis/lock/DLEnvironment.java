package com.mistra.skeleton.redis.lock;

public interface DLEnvironment {

    /**
     * 获取本系统配置的锁前缀
     */
    String prefix();

    /**
     * 是否支持NX锁
     */
    boolean nxLockSupported();

    /**
     * 是否支持红锁
     */
    boolean redLockSupported();

    /**
     * 返回底层技术框架支持，允许用户使用底层api
     */
    DLUnderlyingSupport underlying();
}
