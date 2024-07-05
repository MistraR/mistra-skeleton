/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mistra.skeleton.web.dtp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "spring.dynamic.tp")
public class DTPProperties {

    /**
     * IO密集型线程池核心线程数
     */
    private int corePoolSizeIO = 1;

    /**
     * IO密集型线程池最大线程数
     */
    private int maximumPoolSizeIO = 1;

    /**
     * CPU密集型线程池核心线程数
     */
    private int corePoolSizeCPU = 1;

    /**
     * CPU密集型线程池最大线程数
     */
    private int maximumPoolSizeCPU = 1;

    /**
     * 优先级线程池核心线程数
     */
    private int corePoolSizePriority = 1;

    /**
     * 优先级线程池最大线程数
     */
    private int maximumPoolSizePriority = 1;

    /**
     * future线程池核心线程数
     */
    private int corePoolSizeFuture = 1;

    /**
     * future线程池最大线程数
     */
    private int maximumPoolSizeFuture = 1;

    /**
     * 阻塞队列默认容量
     */
    private int poolQueueSize = 100000;

    /**
     * 是否开启自动扩容核心线程
     */
    private boolean autoExpansion = true;

}
