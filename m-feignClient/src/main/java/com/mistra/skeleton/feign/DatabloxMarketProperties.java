package com.mistra.skeleton.feign;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 中心市场参数
 * @ date: 2024/4/19
 */
@Data
@ConfigurationProperties(prefix = "datablox-market")
public class DatabloxMarketProperties {

    /**
     * 节点请求有效期校验 默认20秒
     */
    private Integer validity = 20;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 中心市场服务地址
     */
    private String server = "http://datablox-market:28080";
}
