package com.mistra.skeleton.mybatis.service;

import com.mistra.skeleton.mybatis.entity.Mistra;
import com.mistra.skeleton.mybatis.mapper.MistraMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Mistra
 * @ Version: 1.0
 * @ Time: 2024/6/30 14:02
 * @ Description:
 * @ Copyright (c) Mistra,All Rights Reserved.
 * @ GitHub: <a href="https://github.com/MistraR">...</a>
 * @ CSDN: <a href="https://blog.csdn.net/axela30w">...</a>
 */
@SpringBootTest
class MistraServiceTest {

    @Autowired
    private MistraService mistraService;
    @Autowired
    private MistraMapper mistraMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    public void selectAll() {
        List<Mistra> mistras = mistraService.selectAll();
        Assertions.assertNotNull(mistras);
    }
}