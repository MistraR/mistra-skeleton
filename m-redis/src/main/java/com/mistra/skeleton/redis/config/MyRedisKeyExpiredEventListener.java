package com.mistra.skeleton.redis.config;

import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/7/24
 */
@Component
public class MyRedisKeyExpiredEventListener implements ApplicationListener<RedisKeyExpiredEvent> {

    @Override
    public void onApplicationEvent(RedisKeyExpiredEvent event) {
        byte[] body = event.getSource();
        System.out.println("获取到延迟消息：" + new String(body));
    }
}
