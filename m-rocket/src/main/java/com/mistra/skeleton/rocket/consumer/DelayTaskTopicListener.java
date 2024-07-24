package com.mistra.skeleton.rocket.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/7/24
 */
@Component
@RocketMQMessageListener(consumerGroup = "Consumer", topic = "DelayTaskTopic")
@Slf4j
public class DelayTaskTopicListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String msg) {
        log.info("获取到延迟任务:{}", msg);
    }
}
