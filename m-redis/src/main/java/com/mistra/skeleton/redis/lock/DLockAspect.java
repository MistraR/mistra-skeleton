package com.mistra.skeleton.redis.lock;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.yamu.framework.dl.util.SpElUtils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author rui.wang
 * @date 2023/7/5
 * @ Description:
 */
@Slf4j
@Aspect
@Component
@Order(0)//确保比事务注解先执行，分布式锁在事务外
public class DLockAspect {

    @Autowired
    private DistributedLockFactory factory;

    @Autowired
    private DLEnvironment dlEnvironment;

    @Around("@annotation(com.yamu.framework.dl.DLock)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DLock redissonLock = method.getAnnotation(DLock.class);
        //默认方法限定名+注解排名（可能多个）
        String prefix = StrUtil.isBlank(redissonLock.prefixKey()) ? SpElUtils.getMethodKey(method) : redissonLock.prefixKey();
        String key = SpElUtils.parseSpEl(method, joinPoint.getArgs(), redissonLock.key());
        key = dlEnvironment.prefix() + ":" + prefix + ":" + key;
        log.info("Acquire distributed lock key [{}]", key);
        if (redissonLock.block()) {
            return factory.executeWithLockThrows(key, redissonLock.leaseTime(),
                    redissonLock.unit(), joinPoint::proceed);
        } else {
            return factory.executeWithTryLockThrows(key, redissonLock.waitTime(), redissonLock.leaseTime(),
                    redissonLock.unit(), joinPoint::proceed);
        }
    }
}
