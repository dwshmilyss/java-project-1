package com.yiban.jdk14.dev;

/**
 * demo1
 *
 * @auther WEI.DUAN
 * @date 2020/6/22
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Demo {
    public static void main(String[] args) {
        test("123");
    }

    public static void test(Object object){
        //由于instanceof的模式匹配是预览功能，需要通过选项--enable-preview --source 14来开启
        // java --enable-preview --source 14 XX.java
        if (object instanceof String str) {
            System.out.println("str = " + str.length());
        }
    }
}
