package com.mistra.skeleton.feign;

import java.util.Date;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import feign.RequestTemplate;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/4/18
 */
@Slf4j
@NoArgsConstructor
public class DefaultMarketFeignInterceptor implements DatabloxMarketFeignInterceptor {

    public static final String TIMESTAMP = "databloxTimestamp";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (requestTemplate.feignTarget().name().equals("datablox-market-server") &&
                requestTemplate.path().startsWith("/api/datablox-market/open")) {
            // 获取请求参数
            byte[] body = requestTemplate.body();
            try {
                if (body.length > 0) {
                    String s = new String(body);
                    JSONObject jsonObject = JSONUtil.parseObj(s);
                    if (!jsonObject.containsKey(TIMESTAMP)) {
                        jsonObject.put(TIMESTAMP, new Date());
                    }
                    String encryptedJsonParams = DatabloxMarketRSAUtil.encryptBlockData(jsonObject.toString());
                    log.info("请求datablox-market url:{},param:{}", requestTemplate.feignTarget().url() + requestTemplate.request().url(),
                            jsonObject);
                    // 将加密后的参数设置回RequestTemplate中
                    requestTemplate.body(encryptedJsonParams);
                } else {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(TIMESTAMP, new Date());
                    String encryptedJsonParams = DatabloxMarketRSAUtil.encrypt(jsonObject.toString());
                    requestTemplate.body(encryptedJsonParams);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
