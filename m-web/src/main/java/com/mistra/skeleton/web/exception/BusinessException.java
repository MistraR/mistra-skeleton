package com.mistra.skeleton.web.exception;

import com.mistra.skeleton.web.response.ResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 11:09
 * @ Description: 业务异常。可以传入args参数。在messages.properties中的占位符会替换为指定的参数值
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@Slf4j
@Data
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7716403813814830930L;

    private Integer code;
    private Object[] args;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, Object... args) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.args = args;
    }

}
