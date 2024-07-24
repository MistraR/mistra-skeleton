package com.mistra.skeleton.rabbit.controller;

import java.util.UUID;
import javax.annotation.Resource;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/7/24
 */
@RestController
@Slf4j
public class RabbitMQDelayTaskController {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/rabbitmq/add")
    public void addTask(@RequestParam("task") String task) throws Exception {
        // 消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        log.info("提交延迟任务");
        // 发送消息
        rabbitTemplate.convertAndSend("sanyouDirectExchangee", "", task, correlationData);
    }
}
