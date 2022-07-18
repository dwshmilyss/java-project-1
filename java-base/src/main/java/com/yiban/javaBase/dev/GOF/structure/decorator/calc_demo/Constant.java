package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 常量函数
 * 一旦使用了关键字strictfp来声明某个类、接口或者方法时，那么在这个关键字所声明的范围内所有浮点运算都是精确的，符合IEEE-754规范的。例如一个类被声明为strictfp，那么该类中所有的方法都是strictfp的。
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Constant extends Function {
    public double constant;

    public Constant(){
        super(new Function[]{});
    }

    public Constant(double constant) {
        super(new Function[]{});
        this.constant = constant;
    }

    @Override
    public double f(double t) {
        return constant;
    }

    public String toString(){
        return Double.toString(constant);
    }

}
