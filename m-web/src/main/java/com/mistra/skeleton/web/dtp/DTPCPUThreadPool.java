package com.mistra.skeleton.web.dtp;

import java.util.concurrent.LinkedBlockingQueue;
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
public class DTPCPUThreadPool extends DTPThreadPool {

    public DTPCPUThreadPool(Integer corePoolSize, Integer poolQueueSize, EnableDiversityThreadPool annotation) {
        init(corePoolSize, poolQueueSize, annotation);
    }

    @Override
    boolean expansion() {
        return threadPoolExecutor.getQueue().size() > threadPoolExecutor.getCorePoolSize() * 300;
    }

    @Override
    void init(Integer corePoolSize, Integer poolQueueSize, EnableDiversityThreadPool annotation) {
        threadPoolExecutor = new ThreadPoolExecutor(
                Math.min(availableProcessors, corePoolSize) + 1,
                Math.min(availableProcessors, corePoolSize) + 1,
                annotation.keepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(poolQueueSize),
                new NamedThreadFactory("CPU-", false));
        threadPoolExecutor.allowCoreThreadTimeOut(annotation.allowCoreThreadTimeOut());
        autoExpansion.set(annotation.autoExpansion());
    }

    @Override
    void executeTask(Runnable task) {
        check();
        threadPoolExecutor.execute(task);
    }

    @Override
    ThreadPoolTaskType poolType() {
        return ThreadPoolTaskType.CPU;
    }

    @Override
    void expandToMaxThread() {
        fixPoolSize(availableProcessors + 2, availableProcessors + 2);
    }
}
