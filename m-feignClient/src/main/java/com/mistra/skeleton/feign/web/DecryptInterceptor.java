package com.mistra.skeleton.feign.web;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.mistra.skeleton.feign.DatabloxMarketProperties;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 只针对开放接口做body参数解密校验
 * @ date: 2024/1/9
 */
@Slf4j
@Component
public class DecryptInterceptor implements HandlerInterceptor {

    public static final String TIMESTAMP = "databloxTimestamp";
    @Autowired
    private DatabloxMarketProperties databloxNodeProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        if (request instanceof RequestWrapper) {
            RequestWrapper requestWrapper = (RequestWrapper) request;
            String body = requestWrapper.getBody();
            try {

                body = DatabloxNodeRSAUtil.decrypt(body, "PrivateKey");
                JSONObject jsonObject = JSONUtil.parseObj(body);
                log.info("{} {}", requestWrapper.getRequestURI(), body);
                if (jsonObject.containsKey(TIMESTAMP)) {
                    Date time = jsonObject.getDate(TIMESTAMP);
                    if (DateUtils.addSeconds(time, databloxNodeProperties.getValidity()).before(new Date())) {
                        log.error("开放接口过期请求 url:{},参数:{}", requestWrapper.getRequestURL(), jsonObject);
                        return false;
                    }
                    requestWrapper.setBody(body);
                } else {
                    log.error("开放接口非法请求 url:{},参数:{}", requestWrapper.getRequestURL(), jsonObject);
                    return false;
                }
            } catch (Exception e) {
                log.error("开放接口参数解密异常,拦截异常请求 密文body:{},error:{}", body, e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest var1, HttpServletResponse var2, Object var3, ModelAndView var4) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest var1, HttpServletResponse var2, Object var3, Exception var4) throws Exception {
    }

}
