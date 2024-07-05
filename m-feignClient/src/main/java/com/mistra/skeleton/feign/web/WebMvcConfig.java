package com.mistra.skeleton.feign.web;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ author: rui.wang@yamu.com
 * @ description:
 * @ date: 2024/1/9
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private DecryptInterceptor decryptInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserinfoInterceptor())
                .excludePathPatterns("/open/**", "/open-chain/**", "/datablox-node/v2/api-docs")
                .addPathPatterns("/**");

        registry.addInterceptor(decryptInterceptor)
                .addPathPatterns("/open/**");

        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Bean
    public FilterRegistrationBean<MistraWebFilter> myFilterFilterRegistrationBean() {
        FilterRegistrationBean<MistraWebFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new MistraWebFilter());
        bean.setOrder(-1);
        List<String> strings = new ArrayList<>();
        strings.add("/datablox-node/open/*");
        strings.add("/api/datablox-node/open/*");
        strings.add("/open/*");

        bean.setUrlPatterns(strings);
        return bean;
    }
}
