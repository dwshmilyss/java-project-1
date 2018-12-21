package com.yiban.btrace.dev;

import java.util.Random;

/**
 * @auther WEI.DUAN
 * @date 2018/11/30
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Demo1 {
    public static void main(String[] args) throws Exception {
//        while (true) {
//            Random random = new Random();
////            execute(random.nextInt(4000));
//            Demo1 demo1 = new Demo1();
//            demo1.sayHello("123456", 123, random.nextInt(4000));
//        }

        Thread.sleep(60000);

        new Demo1().new A().start();

        Thread.sleep(60000);
        new Demo1().new B().run();

    }


    class A extends Thread{
        @Override
        public void run(){
            System.out.println("thread run .... ");
        }
    }

    class B implements Runnable{

        @Override
        public void run() {
            System.out.println("runnable run .... ");
        }
    }

    public static Integer execute(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {
        }
        System.out.println("sleep time is=>" + sleepTime);
        return 0;
    }

    public String sayHello(String name, int age, long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {
        }
        System.out.println("name = " + name + ",age = " + age + ",sleepTime = " + sleepTime);
        return name + " : " + age;
    }

}