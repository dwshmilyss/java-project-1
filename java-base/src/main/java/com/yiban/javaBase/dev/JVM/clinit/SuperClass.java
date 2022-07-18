package com.yiban.javaBase.dev.JVM.clinit;

/**
 * super class
 *
 * @auther WEI.DUAN
 * @date 2018/4/17
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SuperClass {
    protected static int value = 123;

    static
    {
        System.out.println("SuperClass init");
    }
}
