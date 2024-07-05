package com.mistra.skeleton.redis.lock;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/9/21 15:47
 * @ Description:
 */
@FunctionalInterface
public interface Independent {
    void execute();
}
