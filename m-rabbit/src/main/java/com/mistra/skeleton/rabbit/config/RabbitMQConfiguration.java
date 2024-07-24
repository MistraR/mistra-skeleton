package com.mistra.skeleton.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/7/24
 */
@Configuration
public class RabbitMQConfiguration {

    @Bean
    public DirectExchange sanyouDirectExchangee() {
        return new DirectExchange("sanyouDirectExchangee");
    }

    /**
     * 消息发送的时候会将消息发送到sanyouDirectExchange这个交换机上
     * 由于sanyouDirectExchange绑定了sanyouQueue，所以消息会被路由到sanyouQueue这个队列上
     * 由于sanyouQueue没有消费者消费消息，并且又设置了5s的过期时间，所以当消息过期之后，消息就被放到绑定的sanyouDelayTaskExchange死信交换机中
     * 消息到达sanyouDelayTaskExchange交换机后，由于跟sanyouDelayTaskQueue进行了绑定，所以消息就被路由到sanyouDelayTaskQueue中，消费者就能从sanyouDelayTaskQueue中拿到消息了
     *
     * RabbitMQ跟RocketMQ实现有相似之处。
     * 消息最开始都并没有放到最终消费者消费的队列中，而都是放到一个中间队列中，等消息到了过期时间或者说是延迟时间，消息就会被放到最终的队列供消费者消息。
     * 只不过RabbitMQ需要你显示的手动指定消息所在的中间队列，而RocketMQ是在内部已经做好了这块逻辑
     */
    @Bean
    public Queue sanyouQueue() {
        return QueueBuilder
                //指定队列名称，并持久化
                .durable("sanyouQueue")
                //设置队列的超时时间为5秒，也就是延迟任务的时间
                .ttl(5000)
                //指定死信交换机
                .deadLetterExchange("sanyouDelayTaskExchangee")
                .build();
    }

    @Bean
    public Binding sanyouQueueBinding() {
        return BindingBuilder.bind(sanyouQueue()).to(sanyouDirectExchangee()).with("");
    }

    @Bean
    public DirectExchange sanyouDelayTaskExchange() {
        return new DirectExchange("sanyouDelayTaskExchangee");
    }

    @Bean
    public Queue sanyouDelayTaskQueue() {
        return QueueBuilder
                //指定队列名称，并持久化
                .durable("sanyouDelayTaskQueue")
                .build();
    }

    @Bean
    public Binding sanyouDelayTaskQueueBinding() {
        return BindingBuilder.bind(sanyouDelayTaskQueue()).to(sanyouDelayTaskExchange()).with("");
    }
}
