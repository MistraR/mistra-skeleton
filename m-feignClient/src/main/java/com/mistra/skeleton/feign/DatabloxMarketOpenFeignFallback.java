package com.mistra.skeleton.feign;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 中心市场调用异常响应
 * @ date: 2024/4/20
 */
@Slf4j
@Component(value = "databloxMarketOpenFeignFallback")
public class DatabloxMarketOpenFeignFallback implements DatabloxMarketOpenFeignClient {

    private final String logTemplate = "调用中心市场接口 [{}] 失败,param:{}";

    @Override
    public void heartbeat(HeartbeatParam param) {
        log.error(logTemplate, "发送心跳", param);
    }

}
