package com.mistra.skeleton.web.dtp;

import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/5/12 19:03
 * @ Description:
 * > 默认队列大小100000，根据实际情况需要合理设置 spring.dynamic.tp.poolQueueSize
 */
public class DTPContext {

    /**
     * IO密集型线程池
     */
    public static DTPIOThreadPool IO_POOL = null;

    /**
     * CPU密集型线程池
     */
    public static DTPCPUThreadPool CPU_POOL = null;

    /**
     * 优先级线程池
     */
    public static DTPPriorityThreadPool PRIORITY_POOL = null;

    /**
     * Future线程池
     */
    public static DTPFutureThreadPool FUTURE_POOL = null;

    public static void executeIOTask(Runnable task) {
        IO_POOL.executeTask(task);
    }

    public static void executeCPUTask(Runnable task) {
        CPU_POOL.executeTask(task);
    }

    /**
     * 优先级任务 立即执行
     */
    public static void executePriorityTaskImmediately(Runnable task) {
        PRIORITY_POOL.executeTask(new DTPPriorityThreadPool.PriorityTask(task, DTPPriorityThreadPool.TaskPriority.HIGH));
    }

    /**
     * 优先级任务 普通顺序提交
     */
    public static void executePriorityTask(Runnable task) {
        PRIORITY_POOL.executeTask(new DTPPriorityThreadPool.PriorityTask(task, DTPPriorityThreadPool.TaskPriority.LOW));
    }

    /**
     * Future任务
     */
    public static <U> Future<U> submitFutureTask(Supplier<U> supplier) {
        return FUTURE_POOL.submit(supplier);
    }

    public static <U> Future<U> submitFutureTask(Supplier<U> supplier, BiConsumer<U, Throwable> whenComplete) {
        return FUTURE_POOL.submit(supplier, whenComplete);
    }

    public static <U> Future<U> submitFutureTask(Supplier<U> supplier, BiConsumer<U, Throwable> whenComplete,
                                                 Function<Throwable, ? extends U> whenException) {
        return FUTURE_POOL.submit(supplier, whenComplete, whenException);
    }

    public static void shutdown(ThreadPoolTaskType taskType) {
        getByThreadPoolTaskType(taskType).shutdown();
    }

    public static void expandToMaxThread(ThreadPoolTaskType taskType) {
        getByThreadPoolTaskType(taskType).expandToMaxThread();
    }

    public static void reduceToSingleThread(ThreadPoolTaskType taskType) {
        getByThreadPoolTaskType(taskType).reduceToSingleThread();
    }

    private static DTPThreadPool getByThreadPoolTaskType(ThreadPoolTaskType taskType) {
        switch (taskType) {
            case IO:
                return IO_POOL;
            case CPU:
                return CPU_POOL;
            case PRIORITY:
                return PRIORITY_POOL;
            default:
                return FUTURE_POOL;
        }
    }
}
