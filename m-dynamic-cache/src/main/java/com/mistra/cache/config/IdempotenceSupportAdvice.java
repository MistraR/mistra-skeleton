package com.mistra.cache.config;

import com.mistra.cache.util.ReentrantLockUtil;
import com.mistra.skeleton.aop.BnsResultCode;
import com.mistra.skeleton.aop.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/1/10 11:13
 * @ Description:
 */
@Aspect
@Slf4j
@Component
public class IdempotenceSupportAdvice {

    private final Idempotence idempotence;

    private final ReentrantLockUtil reentrantLockUtil;

    private final String IDEMPOTENCE_LOCK_PREFIX = "IDEMPOTENCE_LOCK:";

    public IdempotenceSupportAdvice(Idempotence idempotence, ReentrantLockUtil reentrantLockUtil) {
        this.idempotence = idempotence;
        this.reentrantLockUtil = reentrantLockUtil;
    }

    /**
     * 拦截有@AutoIdempotent注解的方法
     */
    @Pointcut("@annotation(com.yamu.bns.annotation.AutoIdempotent)")
    public void idempotenceMethod() {
    }

    @AfterThrowing(value = "idempotenceMethod()()", throwing = "e")
    public void afterThrowing(Throwable e) {
        // 从HTTP header中获取幂等号idempotenceId
        String idempotenceId = idempotence.getHeaderIdempotenceId();
        if (StringUtils.isNotBlank(idempotenceId))
            idempotence.delete(idempotenceId);
    }

    /**
     * 让第一个请求拿到锁，执行接下来的  验证幂等字符串存在和删除幂等字符串，最后执行业务逻辑，其余请求全部获取锁不成功则直接返回
     *
     * @param joinPoint ProceedingJoinPoint
     * @return joinPoint.proceed()
     * @throws Throwable Throwable
     */
    @Around(value = "idempotenceMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 从HTTP header中获取幂等号idempotenceId
        String idempotenceId = idempotence.getHeaderIdempotenceId();
        if (StringUtils.isEmpty(idempotenceId) ||
                !reentrantLockUtil.tryLock(IDEMPOTENCE_LOCK_PREFIX + idempotenceId, 0, 600) ||
                !idempotence.check(idempotenceId))
            // idempotenceId为空 || 加锁不成功 || 幂等号不存在则直接返回
            throw new BusinessException(BnsResultCode.SYSTEM_INVALID_REQUEST);
        // 删除幂等号
        idempotence.delete(idempotenceId);
        // 执行业务方法
        return joinPoint.proceed();
    }
}
