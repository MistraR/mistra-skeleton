package com.mistra.skeleton.web.dtp;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/5/12 19:34
 * @ Description:
 */
@Slf4j
@Configuration
@ComponentScan(value = "com.mistra.skeleton.web.dtp")
public class DTPFactory {

    private final DTPProperties dtpProperties;

    public DTPFactory(DTPProperties dtpProperties) {
        this.dtpProperties = dtpProperties;
    }

    @EventListener
    public void listener(ApplicationStartedEvent event) {
        EnableDiversityThreadPool annotation = AnnotationUtils.getAnnotation(event.getSpringApplication().getMainApplicationClass(),
                EnableDiversityThreadPool.class);
        if (annotation != null) {
            if (annotation.initCPUPool()) {
                DTPContext.CPU_POOL = new DTPCPUThreadPool(dtpProperties.getCorePoolSizeCPU(),
                        dtpProperties.getPoolQueueSize(), annotation);
                log.info("DtpContext.CPU_POOL init success");
            }
            if (annotation.initIOPool()) {
                DTPContext.IO_POOL = new DTPIOThreadPool(dtpProperties.getCorePoolSizeIO(),
                        dtpProperties.getPoolQueueSize(), annotation);
                log.info("DtpContext.IO_POOL init success");
            }
            if (annotation.initPriorityPool()) {
                DTPContext.PRIORITY_POOL = new DTPPriorityThreadPool(dtpProperties.getCorePoolSizePriority(),
                        dtpProperties.getPoolQueueSize(), annotation);
                log.info("DtpContext.PRIORITY_POOL init success");
            }
            DTPContext.FUTURE_POOL = annotation.initFuturePool() ? new DTPFutureThreadPool(dtpProperties.getCorePoolSizeFuture(),
                    dtpProperties.getPoolQueueSize(), annotation) : new DTPFutureThreadPool();
            log.info("DtpContext.FUTURE_POOL init success");
        }
    }
}
