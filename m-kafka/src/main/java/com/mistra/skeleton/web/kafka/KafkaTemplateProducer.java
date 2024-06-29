package com.mistra.skeleton.web.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/10/19 14:15
 * @ Description:
 */
@Slf4j
@Component
public class KafkaTemplateProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 消息异步发送
     *
     * @param topic   topic
     * @param message message
     */
    public void send(String topic, Object message) {
        // 发送消息
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                // 发送失败的处理
                log.error("Kafka message send error,topic:{},message:{},error:{}", topic, message, throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                // 发送成功的处理
                log.debug("Kafka message send success,topic:{},message:{},result:{}", topic, message, stringObjectSendResult.toString());
            }
        });
    }
}
