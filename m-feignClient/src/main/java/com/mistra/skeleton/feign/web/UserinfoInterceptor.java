package com.mistra.skeleton.feign.web;

import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/1/9
 */
@Slf4j
@Component
public class UserinfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        try {
            if (!(o instanceof HandlerMethod)) {
                return true;
            }
            ApiGlobalVars apiGlobalVars = new ApiGlobalVars();
            apiGlobalVars.setUserId(1L);
            ApiGlobalVarsHolder.setApiGlobalVars(apiGlobalVars);
            log.debug("请求开始,设置用户信息 {}", ApiGlobalVarsHolder.getApiGlobalVars().getUserName());
        } catch (Exception e) {
            ApiGlobalVarsHolder.clear();
            log.error("ERROR-LOG:获取用户信息出错: [{}]", e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest var1, HttpServletResponse var2, Object var3, ModelAndView var4) throws Exception {
        if (Objects.isNull(ApiGlobalVarsHolder.getApiGlobalVars())) {
            log.debug("请求结束,用户信息为空");
            return;
        }
        log.debug("请求结束,清除用户信息 {}", ApiGlobalVarsHolder.getApiGlobalVars().getUserName());
        ApiGlobalVarsHolder.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest var1, HttpServletResponse var2, Object var3, Exception var4) throws Exception {
        if (Objects.isNull(ApiGlobalVarsHolder.getApiGlobalVars())) {
            log.debug("请求结束,用户信息清空");
            return;
        }
        ApiGlobalVarsHolder.clear();
    }

}
