package com.mistra.skeleton.rocket.controller;

import javax.annotation.Resource;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
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
public class RocketMQDelayTaskController {

    @Resource
    private DefaultMQProducer producer;

    /**
     * 延时消息，做订单自动取消
     * 发送消息的时候只需要指定延迟等级即可。如果这18个等级的延迟时间不符和你的要求，可以修改RocketMQ服务端的配置文件
     */
    @GetMapping("/rocketmq/add")
    public void addTask(@RequestParam("task") String task) throws Exception {
        Message msg = new Message("DelayTaskTopic", "TagA", task.getBytes(RemotingHelper.DEFAULT_CHARSET));
        // 延迟等级为2，也就是延迟时间为5s
        msg.setDelayTimeLevel(2);
        // 发送消息并得到消息的发送结果，然后打印
        log.info("提交延迟任务");
        producer.send(msg);
    }
}
