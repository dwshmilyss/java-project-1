package com.yiban.javaBase.dev.reactive;

public class TestLambda {
    public static void main(String[] args) {
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Hello world one!");
            }
        };

        Runnable r2 = () -> System.out.println("Hello world two!");

        r1.run();
        r2.run();
    }
}
