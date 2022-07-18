package com.yiban.javaBase.dev.GOF.structure.decorator.calc_demo;

/**
 * 四则运算
 *
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Arithmetic extends Function {
    protected char op;

    public Arithmetic(char op, Function f1, Function f2) {
        super(new Function[] {f1, f2});
        this.op = op;
    }

    @Override
    public double f(double t) {
        switch (op){
            case '+':
                return sources[0].f(t)+sources[1].f(t);
            case '-':
                return sources[0].f(t)-sources[1].f(t);
            case '*':
                return sources[0].f(t) * sources[1].f(t);
            case '/':
                return sources[0].f(t) / sources[1].f(t);
            default:
                return 0;
        }
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("");
        if (sources.length > 0) {
            buf.append('(');
            buf.append(sources[0]);
            buf.append(Character.toString(op));
            buf.append(sources[1]);
            buf.append(')');
        }
        return buf.toString();
    }


}
