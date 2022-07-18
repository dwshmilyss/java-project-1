package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 计算函数的抽象类。sources：是被装饰的内层函数运算。
 *
 * @auther WEI.DUAN
 * @date 2018/7/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public abstract class Function {
    protected Function[] sources;

    public Function(Function[] sources) {
        this.sources = sources;
    }

    public Function(Function f) {
        this(new Function[] {f});
    }

    public abstract double f(double t);

    public String toString() {
        String name = this.getClass().toString();
        StringBuffer buf = new StringBuffer(name);
        if (sources.length > 0) {
            buf.append('(');
            for (int i=0; i < sources.length; i++) {
                if (i > 0)
                    buf.append(",");
                buf.append(sources[i]);
            }
            buf.append(')');
        }
        return buf.toString();
    }
}
