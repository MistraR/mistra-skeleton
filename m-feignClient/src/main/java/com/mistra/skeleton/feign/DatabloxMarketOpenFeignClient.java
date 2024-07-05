package com.mistra.skeleton.feign;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import feign.RequestInterceptor;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description: DatabloxMarket 网关外部使用
 * @ date: 2024/1/19
 */
@FeignClient(name = "datablox-market-server", url = "${datablox-market.server:http://datablox-market:28080}", configuration =
        DatabloxMarketOpenFeignClient.Config.class, fallback = DatabloxMarketOpenFeignFallback.class, contextId = "datablox-market-server-open")
public interface DatabloxMarketOpenFeignClient {

    @Slf4j
    @Configuration
    @ComponentScan(value = "com.mistra.skeleton.feign")
    @EnableConfigurationProperties(DatabloxMarketProperties.class)
    class Config {

        @Value("${spring.application.name}")
        private String appName;
        private final DatabloxMarketProperties properties;

        public Config(DatabloxMarketProperties properties) {
            this.properties = properties;
        }

        @Bean
        public RequestInterceptor customFeignInterceptor(ObjectProvider<DatabloxMarketFeignInterceptor> interceptorProvider) {
            // 尝试获取DatabloxMarketFeignInterceptor的Bean
            DatabloxMarketFeignInterceptor interceptor = interceptorProvider.getIfAvailable();
            if (interceptor != null) {
                log.info("加载自定义DatabloxMarketFeignInterceptor拦截器");
                return interceptor;
            } else {
                if ("datablox-node-server".equals(appName) && StringUtils.isBlank(properties.getPublicKey())) {
                    throw new RuntimeException("中心市场通信公钥 datablox-market.publicKey 为空");
                }
                if ("datablox-market-server".equals(appName) && StringUtils.isBlank(properties.getPrivateKey())) {
                    throw new RuntimeException("中心市场通信私钥 datablox-market.privateKey 为空");
                }
                log.info("未实现 DatabloxMarketFeignInterceptor，加载默认拦截器 DefaultMarketFeignInterceptor");
                // 如果当前 Spring 容器中不存在DatabloxMarketFeignInterceptor的Bean，则返回默认的拦截器
                return new DefaultMarketFeignInterceptor();
            }
        }
    }

    /**
     * 节点心跳
     */
    @PostMapping("/api/datablox-market/open/v1/heartbeat")
    void heartbeat(@RequestBody @Valid HeartbeatParam param);

}
