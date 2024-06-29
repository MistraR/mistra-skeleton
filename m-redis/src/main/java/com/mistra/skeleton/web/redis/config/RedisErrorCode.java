package com.mistra.skeleton.web.redis.config;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/9/21 16:21
 * @ Description:
 */
public enum RedisErrorCode {
    /**
     *
     */
    SYSTEM_RUNTIME_EXCEPTION("系统异常，请重试"),
    OPERATE_TOO_FREQUENTLY("您操作过于频繁，请稍后再试");

    private final String message;

    public String getErrorMessage() {
        return this.message;
    }

    public String getMessage() {
        return this.message;
    }

    RedisErrorCode(String message) {
        this.message = message;
    }
}
