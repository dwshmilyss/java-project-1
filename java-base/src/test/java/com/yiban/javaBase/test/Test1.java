package com.yiban.javaBase.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2019/5/22
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test1 {
    @Test
    public void test() {
//        System.out.println(Integer.MAX_VALUE);
//        System.out.println(1l << 31);
//        System.out.println("--------------");
//        System.out.println(1 << 30);
//        System.out.println("--------------");
//        System.out.println(Integer.MIN_VALUE);
//        System.out.println(1L << 6);
//        System.out.println(63 >> 6);
//        System.out.println(1 | 1 << 1);
        System.out.println(Math.abs("test_8_3_g2".hashCode()) % 8);
    }

    @Test
    public void test1() {
        List<String> list = new ArrayList<>(1000000);
        long stratTime = System.nanoTime();
        int len = list.size();
        for (int i = 0; i < len; i++) {

        }
        //未优化list耗时：4811
        long endTime = System.nanoTime();
        System.out.println("优化list耗时："+(endTime - stratTime));
    }

    @Test
    public void test2() {
        System.out.println(1>>2);
        int x=2,y=3;
        x = x ^ y;
        y = x ^ y;
        x = x ^ y;
        System.out.println("x = " + x + ",y=" + y);
    }
}