package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.SynchronousQueue;

/**
 * test
 *
 * @auther WEI.DUAN
 * @create 2017/8/19
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class TestSynchronousQueue {
//    private static ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(1);

    /**
     * 有几个生产者线程就需要有几个消费者线程
     * 不然就会一直阻塞
     */
    private static SynchronousQueue<String> queue = new SynchronousQueue<String>();

    public static void main(String[] args) throws Exception {
        new Consumer().start();
        new Consumer().start();
        Thread.sleep(2000);
        new Productor(1).start();
        new Productor(2).start();
        System.out.println("main over.");
    }

    static class Productor extends Thread {
        private int id;

        public Productor(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                String result = "id=" + this.id;
                System.out.println("begin to produce." + result);
                queue.put(result);
                System.out.println("success to produce." + result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("consume begin.");
                String v = queue.take();
                System.out.println("consume success." + v);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
