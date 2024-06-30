package com.mistra.skeleton.mybatis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 13:36
 * @ Description:
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@Data
@TableName("mistra")
public class Mistra {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField(value = "name")
    private String name;
    @TableField(value = "age")
    private Integer age;
    @TableField(value = "status")
    private String status;
}
