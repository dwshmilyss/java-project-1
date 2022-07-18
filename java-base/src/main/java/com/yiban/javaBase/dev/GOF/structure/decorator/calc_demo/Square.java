package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 平方函数
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Square extends Function{

    public Square(){
        super(new Function[]{});
    }

    public Square(Function f) {
        super(new Function[]{f});
    }

    @Override
    public double f(double t) {
        return Math.pow(sources[0].f(t),2);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("");
        if (sources.length > 0) {
            buf.append('(');
            buf.append(sources[0]);
            buf.append('^');
            buf.append(2);
            buf.append(')');
        }
        return buf.toString();
    }
}
