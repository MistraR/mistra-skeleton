package com.mistra.skeleton.feign;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ author: rui.wang@yamu.com
 * @ description: 心跳参数
 * @ date: 2024/6/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HeartbeatParam {

    @ApiModelProperty("bid")
    private String bid;

}
