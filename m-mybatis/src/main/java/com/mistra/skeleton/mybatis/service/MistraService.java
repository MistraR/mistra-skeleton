package com.mistra.skeleton.mybatis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mistra.skeleton.mybatis.entity.Mistra;
import com.mistra.skeleton.mybatis.mapper.MistraMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 13:43
 * @ Description:
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@Service
public class MistraService extends ServiceImpl<MistraMapper, Mistra> {

    public List<Mistra> selectAll() {
        return list(new LambdaQueryWrapper<Mistra>().eq(Mistra::getName, "mistra"));
    }
}
