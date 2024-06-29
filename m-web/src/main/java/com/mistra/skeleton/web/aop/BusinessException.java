package com.mistra.skeleton.web.aop;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务异常。可以传入args参数。在messages.properties中的占位符会替换为指定的参数值
 *
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/10/19 14:08
 * @ Description:
 */
@Slf4j
@Data
public class BusinessException extends RuntimeException {

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

    public BusinessException(BnsResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(BnsResultCode resultCode, Object... args) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.args = args;
    }

}
