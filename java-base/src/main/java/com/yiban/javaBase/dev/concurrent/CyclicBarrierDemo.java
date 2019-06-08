package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @auther WEI.DUAN
 * @date 2019/6/8
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CyclicBarrierDemo {

    private static final int PARTIES = 5;

    public static void main(String[] args) {
        test2();
    }

    /**
     * 设置5个线程互相等待
     */
    static CyclicBarrier cyclicBarrier = new CyclicBarrier(PARTIES);

    /**
     * 设置5个线程互相等待，并且在都到达栅栏后执行一个自定义的任务
     */
    static CyclicBarrier cyclicBarrier1 = new CyclicBarrier(PARTIES, new Runnable() {
        @Override
        public void run() {
            //parties就是到达栅栏的线程数，从当前执行的线程中随机挑选一个线程来执行这个任务
            System.out.println(Thread.currentThread().getName() + ", CyclicBarrier's parties is: " + cyclicBarrier1.getParties());
        }
    });

    /**
     * 测试
     */
    public static void test(){
        for (int i = 0; i < 5; i++) {
            InnerThread innerThread = new InnerThread();
            innerThread.start();
        }
    }

    /**
     * 测试超时
     */
    public static void test1(){
        int N = 4;
        CyclicBarrier barrier  = new CyclicBarrier(N);

        for(int i=0;i<N;i++) {
            if (i < N - 1) {
                new Writer(barrier).start();
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Writer(barrier).start();
            }
        }
    }

    /**
     * 测试重用
     */
    public static void test2(){
        int N = 4;
        CyclicBarrier barrier  = new CyclicBarrier(N);

        for(int i=0;i<N;i++) {
            new Writer(barrier).start();
        }

        try {
            Thread.sleep(25000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("CyclicBarrier重用");

        for(int i=0;i<N;i++) {
            new Writer(barrier).start();
        }


    }

    static class InnerThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                System.out.println(Thread.currentThread().getName() + " wait for CyclicBarrier");
                //将到达栅栏的线程数量+1
                cyclicBarrier1.await();
                // cb的参与者数量等于5时，才继续往后执行
                System.out.println(Thread.currentThread().getName() + " continued.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 模式数据写入
     */
    static class Writer extends Thread{
        private CyclicBarrier cyclicBarrier;
        public Writer(CyclicBarrier cyclicBarrier) {
            this.cyclicBarrier = cyclicBarrier;
        }

        @Override
        public void run() {
            System.out.println("线程"+Thread.currentThread().getName()+"正在写入数据...");
            try {
                Thread.sleep(5000);      //以睡眠来模拟写入数据操作
                System.out.println("线程" + Thread.currentThread().getName() + "写入数据完毕，等待其他线程写入完毕");
                try {
                    cyclicBarrier.await(2000, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {

                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"所有线程写入完毕，继续处理其他任务...");
        }
    }
}