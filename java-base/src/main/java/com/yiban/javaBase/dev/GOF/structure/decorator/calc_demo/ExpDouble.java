package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 指数函数
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ExpDouble extends Function {
    private double  expDouble;  // 指数的底数

    public ExpDouble() {
        super(new Function[] {});
    }

    public ExpDouble(double expDouble, Function f) {
        super(new Function[] {f});
        this.expDouble = expDouble;
    }

    @Override
    public double f(double t) {
        return Math.pow(expDouble, sources[0].f(t));
    }

    public String toString() {

        StringBuffer buf = new StringBuffer("");
        if (sources.length > 0) {
            buf.append('(');
            buf.append('(');
            buf.append(expDouble);
            buf.append(')');
            buf.append('^');
            buf.append(sources[0]);
            buf.append(')');
        }
        return buf.toString();
    }
}
