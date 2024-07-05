package com.mistra.skeleton.web.dtp;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/5/19 16:04
 * @ Description:
 */
@Slf4j
@NoArgsConstructor
public class DTPFutureThreadPool extends DTPThreadPool {

    private ThreadPoolTaskExecutor executor;

    public DTPFutureThreadPool(Integer corePoolSize, Integer poolQueueSize, EnableDiversityThreadPool annotation) {
        init(corePoolSize, poolQueueSize, annotation);
    }

    @Override
    boolean expansion() {
        return executor.getQueueSize() > executor.getCorePoolSize() * 10;
    }

    @Override
    public void fixPoolSize(int corePoolSize, int maxPoolSize) {
        if (Objects.nonNull(executor)) {
            executor.setMaxPoolSize(maxPoolSize);
            executor.setCorePoolSize(corePoolSize);
        }
    }

    @Override
    void init(Integer corePoolSize, Integer poolQueueSize, EnableDiversityThreadPool annotation) {
        executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 设置最大线程数
        executor.setMaxPoolSize(corePoolSize);
        //配置队列大小
        executor.setQueueCapacity(poolQueueSize);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds((int) annotation.keepAliveTime());
        // 设置默认线程名称
        executor.setThreadNamePrefix("FUTURE-");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
    }

    @Override
    void executeTask(Runnable task) {
        if (Objects.nonNull(executor)) {
            executor.submit(task);
        } else {
            CompletableFuture.runAsync(task);
        }
    }

    public <U> Future<U> submit(Supplier<U> supplier) {
        return submit(supplier, (result, e) -> {
        }, e -> {
            e.printStackTrace();
            return null;
        });
    }

    public <U> Future<U> submit(Supplier<U> supplier, BiConsumer<U, Throwable> whenComplete) {
        return submit(supplier, whenComplete, e -> {
            e.printStackTrace();
            return null;
        });
    }

    public <U> Future<U> submit(Supplier<U> supplier, BiConsumer<U, ? super Throwable> whenComplete,
                                Function<Throwable, ? extends U> whenException) {
        if (Objects.nonNull(executor)) {
            if (autoExpansion.get() && expansion()) {
                expandToMaxThread();
                log.info("{} YMFutureThreadPool overstock,thread quantity:{},queue quantity:{}", poolType(),
                        executor.getActiveCount(), executor.getQueueSize());
            }
            return CompletableFuture.supplyAsync(supplier, executor).whenComplete(whenComplete).exceptionally(whenException);
        } else {
            // 若未实例化executor则默认使用ForkJoinPool
            return CompletableFuture.supplyAsync(supplier).whenComplete(whenComplete).exceptionally(whenException);
        }
    }

    @Override
    ThreadPoolTaskType poolType() {
        return ThreadPoolTaskType.FUTURE;
    }

    @Override
    void expandToMaxThread() {
        fixPoolSize(availableProcessors + 2, availableProcessors + 2);
    }

    @Override
    protected void shutdown() {
        executor.shutdown();
    }
}
