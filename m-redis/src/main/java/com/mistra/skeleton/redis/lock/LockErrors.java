package com.mistra.skeleton.redis.lock;

/**
 * 星火链相关的异常枚举值 基础架构（1000）、分布式锁（01）、分布式锁子模块（01）、序列号
 */
public enum LockErrors {

    /*
    系统异常
     */
    LOCK_ERROR("100002010001", "加锁失败"),
    REDIS_ERROR("100002010002", "缓存访问失败"),
    OPERATE_TOO_FREQUENTLY("100002010003", "您操作过于频繁，请稍后再试");

    private final String code;
    private final String description;

    LockErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String description(Object... objs) {
        return String.format(description, objs);
    }
}
