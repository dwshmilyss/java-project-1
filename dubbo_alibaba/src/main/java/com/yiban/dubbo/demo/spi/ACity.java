package com.yiban.dubbo.demo.spi;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/8/23
 **/
public class ACity implements City {
    @Override
    public void getName() {
        System.out.println("this is ACity");
    }
}
