package com.mistra.skeleton.web.exception;

import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.mistra.skeleton.web.response.InternationalizationUtil;
import com.mistra.skeleton.web.response.ResponseResult;
import com.mistra.skeleton.web.response.ResultCode;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 11:09
 * @ Description: 全局异常处理
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@Slf4j
@RestControllerAdvice
@ControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice {

    @Autowired
    private InternationalizationUtil i18nUtil;

    /**
     * 业务异常返回结果
     *
     * @param businessException 业务异常
     * @return BnsResponseResult
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public ResponseResult handler(BusinessException businessException) {
        String message = businessException.getMessage();
        Integer errorCode = businessException.getCode();
        if (Objects.isNull(errorCode)) {
            errorCode = ResultCode.SYSTEM_RUNTIME_EXCEPTION.getCode();
        }
        String resultMessage = i18nUtil.i18n(errorCode + "", businessException.getArgs());
        log.info("业务异常:{}-{}-{}", errorCode, message, resultMessage, businessException);
        return new ResponseResult(errorCode, message);
    }

    /**
     * 参数验证异常
     *
     * @param e BindException
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public ResponseResult handler(BindException e) {
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        return new ResponseResult(ResultCode.PARAM_NOT_VALID_EXCEPTION.getCode(), objectErrors.get(0).getDefaultMessage());
    }

    /**
     * 参数验证异常
     *
     * @param e BindException
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult handler(MethodArgumentNotValidException e) {
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        return new ResponseResult(ResultCode.PARAM_NOT_VALID_EXCEPTION.getCode(), objectErrors.get(0).getDefaultMessage());
    }

    /**
     * @param e        异常
     * @param response HttpServletResponse
     * @return BnsResponseResult
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseResult globalException(Exception e, HttpServletResponse response) {
        e.printStackTrace();
        return new ResponseResult(ResultCode.SYSTEM_RUNTIME_EXCEPTION.getCode(), e.getMessage());
    }

}
