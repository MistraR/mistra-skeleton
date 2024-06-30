package com.mistra.skeleton.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 11:09
 * @ Description:
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 允许跨域
     *
     * @param registry CorsRegistry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(1800)
                .allowedOrigins("*");
    }
}
