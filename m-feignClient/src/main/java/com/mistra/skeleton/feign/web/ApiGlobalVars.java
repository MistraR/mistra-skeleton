package com.mistra.skeleton.feign.web;

import lombok.Data;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 全局参数
 * @ date: 2024/1/9
 */
@Data
public class ApiGlobalVars {

    /**
     * 当前用户id
     */
    private Long userId;

    /**
     * 当前用户昵称
     */
    private String userName;

}
