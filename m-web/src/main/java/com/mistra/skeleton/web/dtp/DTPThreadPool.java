package com.mistra.skeleton.web.dtp;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/5/13 15:51
 * @ Description:
 */
@Slf4j
public abstract class DTPThreadPool {

    public static final int availableProcessors = Runtime.getRuntime().availableProcessors();

    protected ThreadPoolExecutor threadPoolExecutor;

    public AtomicBoolean autoExpansion = new AtomicBoolean(true);

    /**
     * 扩容核心线程数
     */
    private void flexible() {
        if (threadPoolExecutor.getCorePoolSize() < availableProcessors * 2
                && getMemoryUsage() < 0.8) {
            threadPoolExecutor.setMaximumPoolSize(threadPoolExecutor.getMaximumPoolSize() + 1);
            threadPoolExecutor.setCorePoolSize(threadPoolExecutor.getCorePoolSize() + 1);
        }
    }

    /**
     * 提供给外部自定义修改线程池核心参数
     */
    public void fixPoolSize(int corePoolSize, int maxPoolSize) {
        if (maxPoolSize < corePoolSize) {
            throw new RuntimeException("thread pool maxPoolSize must gt corePoolSize");
        }
        ready();
        threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
        threadPoolExecutor.setCorePoolSize(corePoolSize);
    }

    private void ready() {
        Assert.state(this.threadPoolExecutor != null,
                this.getClass() + " not initialized,Please add annotation @EnableDiversityThreadPool on Application class");
    }

    /**
     * 检查线程池是否需要扩容
     */
    protected void check() {
        ready();
        if (autoExpansion.get() && expansion()) {
            log.info("{} YMThreadPool overstock,thread quantity:{},queue quantity:{}", poolType(),
                    threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size());
            flexible();
        }
    }

    /**
     * 是否触发扩容条件
     */
    abstract boolean expansion();

    /**
     * 初始化线程池
     */
    abstract void init(Integer corePoolSize, Integer poolQueueSize, EnableDiversityThreadPool annotation);

    /**
     * 提交任务
     */
    abstract void executeTask(Runnable task);

    /**
     * 线程池类型
     */
    abstract ThreadPoolTaskType poolType();

    /**
     * JVM内存使用率
     */
    private static double getMemoryUsage() {
        return (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().maxMemory();
    }

    /**
     * 膨胀至最大线程
     */
    abstract void expandToMaxThread();

    /**
     * 缩小至单线程
     */
    protected void reduceToSingleThread() {
        autoExpansion.set(false);
        fixPoolSize(1, 1);
    }

    /**
     * 关闭线程池
     */
    protected void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
