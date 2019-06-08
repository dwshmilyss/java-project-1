package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.Semaphore;

/**
 * @auther WEI.DUAN
 * @date 2019/6/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        //工人数
        int N = 8;
        Semaphore semaphore = new Semaphore(5);
        for (int i = 0; i < N; i++) {
            new Worker(i, semaphore).run();
        }
    }

    static class Worker implements Runnable {
        private int num;
        private Semaphore semaphore;

        public Worker(int num, Semaphore semaphore) {
            this.num = num;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println("工人："+this.num+"占用一个机器在生产....");
                Thread.sleep(2000);
                System.out.println("工人："+this.num+"使用完毕，释放机器....");
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}