package com.yiban.javaBase.dev.concurrent;

/**
 * interrupt demo
 *
 * @auther WEI.DUAN
 * @create 2017/10/28
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class InterruptDemo {

    public static void main(String[] args) {
        InnerClass innerClass = new InterruptDemo().new InnerClass();
        innerClass.run();

    }

    class InnerClass extends Thread {

        @Override
        public void run() {
            int i = 0;
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(i++);
            }
        }
    }
}
