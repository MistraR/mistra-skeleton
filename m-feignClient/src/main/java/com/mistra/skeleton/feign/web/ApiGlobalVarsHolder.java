package com.mistra.skeleton.feign.web;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 全局变量TL
 * @ date: 2024/1/9
 */
public class ApiGlobalVarsHolder {

    private static final ThreadLocal<ApiGlobalVars> THREAD_LOCAL = new InheritableThreadLocal<>();

    public static ApiGlobalVars getApiGlobalVars() {
        return THREAD_LOCAL.get();
    }

    public static void setApiGlobalVars(final ApiGlobalVars apiGlobalVars) {
        THREAD_LOCAL.set(apiGlobalVars);
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }
}
