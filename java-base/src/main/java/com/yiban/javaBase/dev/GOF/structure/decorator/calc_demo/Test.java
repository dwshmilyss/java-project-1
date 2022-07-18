package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 测试类
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        Function complexFunc = new Arithmetic('+', new Square(new Sin(new T())), new Square(new Cos(new T())));
        System.out.println(complexFunc + " = " + complexFunc.f(100.0));
    }
}
