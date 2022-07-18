package com.yiban.javaBase.dev.GOF.structure.proxy.dynamic_porxy.demo2;

public interface IDao {
    public void show();

    public Object show2();
}

class DaoImpl implements IDao {

    @Override
    public void show() {
        System.out.println("我是show");
    }

    @Override
    public Object show2() {
        System.out.println("我是show2");
        return null;
    }
}
