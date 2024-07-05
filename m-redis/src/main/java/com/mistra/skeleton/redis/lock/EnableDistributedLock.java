package com.mistra.skeleton.redis.lock;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/4/15 16:43
 * @ Description:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DistributedLockAutoConfiguration.class)
public @interface EnableDistributedLock {
}
