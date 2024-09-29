package com.mistra.skeleton.kafka;

import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

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
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                System.out.println("Sent message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                System.out.println("Unable to send message=["
                        + message + "] due to : " + ex.getMessage());
            }
        });
    }
}
