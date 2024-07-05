package com.mistra.skeleton.redis.lock;

import lombok.experimental.StandardException;

public class LockException extends Exception {

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

    public String errorCode() {
        return code;
    }

    public String errorMessage() {
        return getMessage();
    }
}
