package com.mistra.skeleton.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ author: rui.wang@yamu.com
 * @ description: DatabloxMarket 网关内部使用
 * @ date: 2024/4/19
 */
@FeignClient(name = "datablox-market-server", fallback = DatabloxMarketFeignFallback.class, contextId = "datablox-market-server")
public interface DatabloxMarketFeignClient {
}
