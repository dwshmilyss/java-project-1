package com.yiban.javaBase.test;

public class SubThreadLocal<T> extends ThreadLocal {

    @Override
    protected Object initialValue() {
        return false;
    }
}
