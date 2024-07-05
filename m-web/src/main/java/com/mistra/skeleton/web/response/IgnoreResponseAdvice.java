package com.mistra.skeleton.web.response;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 不需要MistraResponseAdvice拦截的响应体使用此注解标记
 * @ date: 2024/7/5
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreResponseAdvice {
}
