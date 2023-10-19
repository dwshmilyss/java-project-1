package com.yiban.javaBase.dev.concurrent;

public class ThreadLocalDemo {
    public static final ThreadLocal userThreadLocal = new ThreadLocal();

    public static ThreadLocal<Integer> counter = new ThreadLocal<Integer>(){

        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    public static void increment() {
        counter.set(counter.get() + 1);
    }

    public static int getCount() {
        return counter.get();
    }

    public static void main(String[] args) {
//        userThreadLocal.set("aa");
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    increment();
                    System.out.println("thread name = " + Thread.currentThread().getName() + ",counter = " + getCount());
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    increment();
                    System.out.println("thread name = " + Thread.currentThread().getName() + ",counter = " + getCount());
                }
            }
        });
        t1.start();
        t2.start();
    }
}
