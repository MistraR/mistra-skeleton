package com.mistra.skeleton.web.response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 11:09
 * @ Description: 国际化语言工具类
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@Component
public class InternationalizationUtil {

    @Autowired
    private MessageSource messageSource;

    /**
     * 根据errorCode和本地化对象Local获取国际化提示信息
     *
     * @param errorCode errorCode
     * @return String
     */
    public String i18n(int errorCode) {
        return i18n(String.valueOf(errorCode));
    }

    public String i18n(String errorCode) {
        return messageSource.getMessage(errorCode, null, errorCode, LocaleContextHolder.getLocale());
    }

    public String i18n(String errorCode, Object[] args) {
        return messageSource.getMessage(errorCode, args, LocaleContextHolder.getLocale());
    }
}
