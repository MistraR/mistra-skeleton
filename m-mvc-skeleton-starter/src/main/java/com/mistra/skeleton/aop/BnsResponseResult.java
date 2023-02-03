package com.mistra.skeleton.aop;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

/**
 * 返回值处理类。封装controller层的返回值给前端。
 *
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/10/19 14:08
 * @ Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BnsResponseResult {

    @ApiModelProperty(value = "返回码 0表示成功，非0则表示异常", name = "code", example = "0")
    @Builder.Default
    private Integer code = BnsResultCode.SUCCESS.getCode();
    @ApiModelProperty(value = "处理消息", name = "msg", example = "处理成功")
    @Builder.Default
    private String msg = BnsResultCode.SUCCESS.getMessage();
    @ApiModelProperty(value = "返回数据", name = "data", example = "{}")
    @Builder.Default
    private Object data = "{}";
    @ApiModelProperty(value = "唯一请求id，由logback生成", name = "requestId", example = "a1b2c3d4e5f6g7h8i9")
    private String requestId = MDC.get("requestId");

    public BnsResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BnsResponseResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 默认构建成功请求  默认code=0 data=null
     */
    public static BnsResponseResult buildSuccess() {
        return new BnsResponseResult(BnsResultCode.SUCCESS.getCode(), BnsResultCode.SUCCESS.getMessage());
    }

    public static BnsResponseResult buildFail() {
        return new BnsResponseResult(BnsResultCode.SYSTEM_RUNTIME_EXCEPTION.getCode(), BnsResultCode.SYSTEM_RUNTIME_EXCEPTION.getMessage());
    }

    public static BnsResponseResult buildSuccess(Object data) {
        return new BnsResponseResult(BnsResultCode.SUCCESS.getCode(), BnsResultCode.SUCCESS.getMessage(), data);
    }

    public BnsResponseResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

}
