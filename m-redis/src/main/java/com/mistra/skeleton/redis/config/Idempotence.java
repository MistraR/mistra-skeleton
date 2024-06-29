package com.mistra.skeleton.redis.config;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/1/10 11:06
 * @ Description:
 */
public interface Idempotence {

    /**
     * 检查是否存在幂等号
     *
     * @param idempotenceId 幂等号
     * @return 是否存在
     */
    boolean check(String idempotenceId);

    /**
     * 记录幂等号
     *
     * @param idempotenceId 幂等号
     */
    void record(String idempotenceId);

    /**
     * 记录幂等号
     *
     * @param idempotenceId 幂等号
     * @param time          过期时间
     */
    void record(String idempotenceId, Integer time);

    /**
     * 删除幂等号
     *
     * @param idempotenceId 幂等号
     */
    void delete(String idempotenceId);

    /**
     * 生成幂等号
     *
     * @return uId
     */
    String generateId();

    /**
     * 从Header里面获取幂等号
     *
     * @return idempotenceId
     */
    String getHeaderIdempotenceId();
}
