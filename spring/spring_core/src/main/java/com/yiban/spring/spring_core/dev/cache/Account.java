package com.yiban.spring.spring_core.dev.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 测试实体类
 *
 * @auther WEI.DUAN
 * @date 2019/11/1
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Account {
    @Getter
    @Setter
    private int id;
    @Getter
    @Setter
    private String name;

    public Account(String name) {
        this.name = name;
    }
}