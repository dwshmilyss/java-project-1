package com.yiban.javaBase.dev.concurrent;

public class ThreadLocalDemo {
    public static final ThreadLocal userThreadLocal = new ThreadLocal();

    public static void main(String[] args) {
        userThreadLocal.set("aa");
    }
}
