package com.mistra.skeleton.feign.web;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/1/9
 */
@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    private static final HashSet<String> transferHeader = new HashSet<>();

    static {
        transferHeader.add(SecurityConstants.AUTHORIZATION_HEADER.toUpperCase());
        transferHeader.add(SecurityConstants.DETAILS_USER_ID.toUpperCase());
        transferHeader.add(SecurityConstants.DETAILS_USERNAME.toUpperCase());
    }

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest request = getHttpServletRequest();
        if (Objects.isNull(request)) {
            return;
        }
        Map<String, String> headers = getHeaders(request);
        if (headers.size() > 0) {
            Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                // 把请求过来的header请求头 原样设置到feign请求头中
                if (transferHeader.contains(entry.getKey().toUpperCase())) {
                    template.header(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enums = request.getHeaderNames();
        while (enums.hasMoreElements()) {
            String key = enums.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
