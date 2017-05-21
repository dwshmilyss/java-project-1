package com.yiban.hive.dev;

/**
 * @auther WEI.DUAN
 * @create 2017/5/16
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(Test.class.getClassLoader().getResource("jdbc.properties"));
    }
}
