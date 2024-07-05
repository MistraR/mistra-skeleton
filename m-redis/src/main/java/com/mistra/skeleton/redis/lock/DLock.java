package com.mistra.skeleton.redis.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author rui.wang
 * @date 2023/7/5
 * @ Description: 分布式锁注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DLock {

    /**
     * key的前缀,默认取方法全限定名，除非我们在不同方法上对同一个资源做分布式锁，就自己指定
     *
     * @return key的前缀
     */
    String prefixKey() default "";

    /**
     * springEl 表达式
     *
     * @return 表达式
     */
    String key();

    /**
     * 获取锁是否阻塞，默认不阻塞
     *
     * @return boolean
     */
    boolean block() default false;

    /**
     * 等待获取锁的时间，默认-1，不等待直接失败
     *
     * @return 单位秒
     */
    int waitTime() default -1;

    /**
     * 锁的释放时间，默认9000毫秒
     *
     * @return 单位秒
     */
    int leaseTime() default 9000;

    /**
     * 等待锁的时间单位，默认毫秒
     *
     * @return 单位
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
