package com.mistra.cache.config;

import java.lang.annotation.*;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/1/10 11:11
 * @ Description: 幂等校验
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoIdempotent {
}
