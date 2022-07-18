package com.yiban.javaBase.dev.syntax;

/**
 * null demo
 * 主要是测试Null和字符串拼接，需要使用javap -c NullDemo查看字节码
 *
 * @auther WEI.DUAN
 * @date 2018/1/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class NullDemo {
    public static void main(String[] args) {
        String s = null;
        s = s + "!";
        //输出null! (null被隐式转换成字符串)
        System.out.println(s);

        int a = 0;
        System.out.println(a == 0 ? (a = 1) + (a + 1) : 0);
    }
}
