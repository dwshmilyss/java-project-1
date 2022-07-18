package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 线性函数
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class T extends Function {
    public T(){
        super(new Function[]{});
    }

    @Override
    public double f(double t) {
        return t;
    }

    public String toString(){
        return "t";
    }
}
