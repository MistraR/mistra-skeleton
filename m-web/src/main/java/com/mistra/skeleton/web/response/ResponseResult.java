package com.mistra.skeleton.web.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 11:09
 * @ Description: 返回值处理类。封装controller层的返回值给前端
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult {

    @ApiModelProperty(value = "返回码 0表示成功，非0则表示异常", name = "code", example = "0")
    @Builder.Default
    private Integer code = ResultCode.SUCCESS.getCode();
    @ApiModelProperty(value = "处理消息", name = "msg", example = "处理成功")
    @Builder.Default
    private String msg = ResultCode.SUCCESS.getMessage();
    @ApiModelProperty(value = "返回数据", name = "data", example = "{}")
    @Builder.Default
    private Object data = "{}";
    @ApiModelProperty(value = "唯一请求id，由logback生成", name = "requestId", example = "a1b2c3d4e5f6g7h8i9")
    private String requestId = MDC.get("requestId");

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 默认构建成功请求  默认code=0 data=null
     */
    public static ResponseResult buildSuccess() {
        return new ResponseResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static ResponseResult buildFail() {
        return new ResponseResult(ResultCode.SYSTEM_RUNTIME_EXCEPTION.getCode(), ResultCode.SYSTEM_RUNTIME_EXCEPTION.getMessage());
    }

    public static ResponseResult buildSuccess(Object data) {
        return new ResponseResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public ResponseResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

}
