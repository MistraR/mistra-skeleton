package com.mistra.skeleton.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

/**
 * 全局异常处理
 *
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/10/19 14:08
 * @ Description:
 */
@Slf4j
@RestControllerAdvice
@ControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice {

    /**
     * 业务异常返回结果
     *
     * @param businessException 业务异常
     * @return BnsResponseResult
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public BnsResponseResult handler(BusinessException businessException) {
        String message = businessException.getMessage();
        Integer errorCode = businessException.getCode();
        if (Objects.isNull(errorCode)) {
            errorCode = BnsResultCode.SYSTEM_RUNTIME_EXCEPTION.getCode();
        }
        return new BnsResponseResult(errorCode, message);
    }

    /**
     * 参数验证异常
     *
     * @param e BindException
     */
    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public BnsResponseResult handler(BindException e) {
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        return new BnsResponseResult(BnsResultCode.PARAM_NOT_VALID_EXCEPTION.getCode(), objectErrors.get(0).getDefaultMessage());
    }

    /**
     * 参数验证异常
     *
     * @param e BindException
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BnsResponseResult handler(MethodArgumentNotValidException e) {
        List<ObjectError> objectErrors = e.getBindingResult().getAllErrors();
        return new BnsResponseResult(BnsResultCode.PARAM_NOT_VALID_EXCEPTION.getCode(), objectErrors.get(0).getDefaultMessage());
    }

    /**
     * @param e        异常
     * @param response HttpServletResponse
     * @return BnsResponseResult
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public BnsResponseResult globalException(Exception e, HttpServletResponse response) {
        e.printStackTrace();
        return new BnsResponseResult(BnsResultCode.SYSTEM_RUNTIME_EXCEPTION.getCode(), e.getMessage());
    }

}
