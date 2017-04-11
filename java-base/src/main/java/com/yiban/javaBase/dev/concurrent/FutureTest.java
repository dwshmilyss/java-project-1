package com.yiban.javaBase.dev.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by duanwei on 2017/3/13.
 */
public class FutureTest {
    public static void main(String[] args) {
        testFuture();
    }

    public static void testFuture() {
        ExecutorService pool = Executors.newCachedThreadPool();

        List<Future<String>> futureList = new ArrayList<Future<String>>();

        for (int i = 0; i < 10; i++) {
            futureList.add(pool.submit(new InnerClass(i)));
        }
//        pool.shutdownNow();


        for (Future future : futureList
                ) {
            try {
                Object obj = future.get();
                System.out.println(obj.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
//                pool.shutdown();
                try {
                    pool.awaitTermination(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static final class InnerClass implements Callable {
        private int id;

        public InnerClass(int id) {
            this.id = id;
        }

        public String call() throws Exception {
            System.out.println(Thread.currentThread().getName() + " 开始工作,id : " + id);
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName()+" ： "+i);
            }
            return Thread.currentThread().getName() + " 完成工作,id : " + id;
        }
    }

    static final class RunnableInnerClass implements Runnable {
        private int id;

        public RunnableInnerClass(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println(Thread.currentThread().getName() + " 开始工作,id : " + id);
            for (int i = 0; i < 100; i++) {

            }
            System.out.println(Thread.currentThread().getName() + " 完成工作,id : " + id);
        }
    }
}
