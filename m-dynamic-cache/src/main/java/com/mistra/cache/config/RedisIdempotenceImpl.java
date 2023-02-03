package com.mistra.cache.config;

import com.mistra.cache.util.RedisUtil;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/1/10 11:06
 * @ Description:
 */
@Service
public class RedisIdempotenceImpl implements Idempotence {

    private final RedisUtil redisUtil;

    public RedisIdempotenceImpl(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public boolean check(String idempotenceId) {
        return redisUtil.isKeyExist(idempotenceId);
    }

    @Override
    public void record(String idempotenceId) {
        redisUtil.set(idempotenceId, "1");
    }

    @Override
    public void record(String idempotenceId, Integer time) {
        redisUtil.setex(idempotenceId, "1", time);
    }

    @Override
    public void delete(String idempotenceId) {
        redisUtil.deleteKey(idempotenceId);
    }

    @Override
    public String generateId() {
        String uuid = UUID.randomUUID().toString();
        String uId = Base64Util.encode(uuid).toLowerCase();
        redisUtil.setex(uId, "1", 86400);
        return uId;
    }

    @Override
    public String getHeaderIdempotenceId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        return request.getHeader("idempotenceId");
    }

}
