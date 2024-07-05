package com.mistra.skeleton.web.dtp;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/4/15 16:43
 * @ Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({DTPFactory.class})
public @interface EnableDiversityThreadPool {

    /**
     * 初始化IO密集型线程池
     */
    boolean initIOPool() default true;

    /**
     * 初始化CPU密集型线程池
     */
    boolean initCPUPool() default true;

    /**
     * 初始化优先级调度线程池
     */
    boolean initPriorityPool() default true;

    /**
     * 初始化future任务线程池，若关闭，则默认使用ForkJoinPool
     */
    boolean initFuturePool() default true;

    /**
     * 自动扩容核心线程
     */
    boolean autoExpansion() default true;

    /**
     * 是否回收核心线程
     */
    boolean allowCoreThreadTimeOut() default false;

    /**
     * 线程空闲存活时间，超过则回收 单位:TimeUnit.SECONDS
     */
    long keepAliveTime() default 5;
}
