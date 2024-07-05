package com.mistra.skeleton.redis.lock;

import com.yamu.framework.common.StandardException;
import com.yamu.framework.common.StandardStringEnum;

public class LockException extends Exception implements StandardException {

    private String code = LockErrors.LOCK_ERROR.code();

    private StandardException cause;

    public LockException(String message) {
        super(message);
        this.cause = null;
    }

    public LockException(String message, Throwable cause) {
        super(message, cause);
        this.cause = null;
    }

    public LockException(Throwable cause) {
        super(cause);
        this.cause = null;
    }

    public LockException(StandardStringEnum cause, Object... objs) {
        super(cause.description(objs));
        this.code = cause.code();
    }

    @Override
    public String errorCode() {
        return code;
    }

    @Override
    public String errorMessage() {
        return getMessage();
    }
}
