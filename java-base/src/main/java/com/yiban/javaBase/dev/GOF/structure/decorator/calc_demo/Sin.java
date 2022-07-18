package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 正弦函数
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Sin extends Function {
    public Sin(Function f) {
        super(new Function[]{f});
    }

    public Sin(){
        super(new Function[]{});
    }

    @Override
    public double f(double t) {
        return Math.sin(sources[0].f(t));
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("");
        if (sources.length > 0) {
            buf.append('(');
            buf.append("sin(");
            buf.append(sources[0]);
            buf.append(')');
            buf.append(')');
        }
        return buf.toString();
    }
}
