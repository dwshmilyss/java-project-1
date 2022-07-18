package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 幂函数
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Pow extends Function {
    private double  pow;  // 幂函数的指数

    public Pow() {
        super(new Function[] {});
    }

    public Pow(Function f, double pow) {
        super(new Function[] {f});
        this.pow = pow;
    }

    @Override
    public double f(double t) {
        return Math.pow(sources[0].f(t), pow);
    }

    public String toString() {

        StringBuffer buf = new StringBuffer("");
        if (sources.length > 0) {
            buf.append('(');
            buf.append(sources[0]);
            buf.append('^');
            buf.append('(');
            buf.append(pow);
            buf.append(')');
            buf.append(')');
        }
        return buf.toString();
    }
}
