package com.mistra.skeleton.web.dtp;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/5/13 15:57
 * @ Description:
 */
@Slf4j
public class DTPPriorityThreadPool extends DTPThreadPool {

    private final PriorityBlockingQueue<Runnable> priorityBlockingQueue;

    public DTPPriorityThreadPool(Integer corePoolSize, Integer poolQueueSize, EnableDiversityThreadPool annotation) {
        priorityBlockingQueue = new PriorityBlockingQueue(poolQueueSize, Comparator.comparingLong((PriorityTask t) -> t.priority));
        init(corePoolSize, poolQueueSize, annotation);
    }

    /**
     * 自定义默认拒绝执行处理器：让调用线程执行最先提交到队列的任务，然后提交当前任务
     */
    private static final RejectedExecutionHandler defaultHandler = (r, e) -> {
        if (e.isShutdown()) {
            return;
        }
        Optional.ofNullable(e.getQueue().poll()).ifPresent(Runnable::run);
        e.execute(r);
    };

    /**
     * 优先级任务
     */
    public static class PriorityTask implements Runnable {
        private final Runnable task;
        private final long priority;

        public PriorityTask(Runnable task, TaskPriority priority) {
            this.task = task;
            this.priority = priority.value;
        }

        @Override
        public void run() {
            task.run();
        }
    }

    /**
     * 任务优先级枚举：HIGH > MEDIUM > LOW
     */
    public enum TaskPriority {
        HIGH(1L),
        MEDIUM(2L),
        LOW(3L);

        private final Long value;

        TaskPriority(Long value) {
            this.value = value;
        }
    }

    @Override
    boolean expansion() {
        return threadPoolExecutor.getQueue().size() > threadPoolExecutor.getCorePoolSize() * 10;
    }

    @Override
    void init(Integer corePoolSize, Integer poolQueueSize, EnableDiversityThreadPool annotation) {
        threadPoolExecutor = new ThreadPoolExecutor(
                Math.min(availableProcessors, corePoolSize),
                Math.min(availableProcessors, corePoolSize),
                annotation.keepAliveTime(), TimeUnit.SECONDS,
                priorityBlockingQueue,
                new NamedThreadFactory("PRIORITY-", false), defaultHandler);
        threadPoolExecutor.allowCoreThreadTimeOut(annotation.allowCoreThreadTimeOut());
        autoExpansion.set(annotation.autoExpansion());
    }

    @Override
    void executeTask(Runnable task) {
        if (task instanceof PriorityTask) {
            check();
            threadPoolExecutor.execute(task);
        } else {
            throw new RuntimeException("not a priority task");
        }
    }

    @Override
    ThreadPoolTaskType poolType() {
        return ThreadPoolTaskType.PRIORITY;
    }

    @Override
    void expandToMaxThread() {
        fixPoolSize(availableProcessors + 2, availableProcessors + 2);
    }
}
