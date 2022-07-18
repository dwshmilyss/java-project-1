package com.yiban.javaBase.dev.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 启动10个线程执行任务，用future接收返回结果并打印
 * Created by duanwei on 2017/3/13.
 */
public class FutureTest {
    public static void main(String[] args) {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
//        testFuture(pool);
        test1(fixedThreadPool);
    }

    /**
     * 测试接收线程返回值
     *
     * @param pool 线程池
     */
    public static void testFuture(ExecutorService pool) {
        //可以接收线程的返回值
        List<Future<String>> futureList = new ArrayList();

        //提交所有的任务
        for (int i = 0; i < 10; i++) {
            futureList.add(pool.submit(new CallableClass(i)));
        }

        //遍历线程的返回结果
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
                try {
                    pool.awaitTermination(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 一个主线程下有多个子线程任务，主线程必须在100秒内将子线程执行的集合结果进行处理返回
     *
     * @param pool
     */
    static void test1(ExecutorService pool) {
        List<CallableClass> tasks = new ArrayList();
        //提交所有的任务
        for (int i = 0; i < 10; i++) {
            tasks.add(new CallableClass(i));
        }
        long begin = 0;
        long end = 0;
        begin = System.currentTimeMillis();
        try {
            /**
             * invokeAll(tasks,timeout,TimeUnit)
             * tasks ： 一组任务的结婚
             * timeout：规定任务执行的超时时间，超过该时间的任务会被cancel，可以通过捕获CancellationException的异常来获取被取消的任务
             * TimeUnit：时间单位
             */
            List<Future<String>> futureList = pool.invokeAll(tasks, 10, TimeUnit.SECONDS);
            Iterator<CallableClass> taskIter = tasks.iterator();
            for (Future future : futureList
                    ) {
                CallableClass task = taskIter.next();
                try {
                    Object obj = future.get();
                    System.out.println(obj);
                } catch (ExecutionException e) {
                    System.out.println("ExecutionException task id = " + task.id);
                } catch (CancellationException e) {
                    System.out.println("CancellationException task id = " + task.id);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
//                pool.awaitTermination(5, TimeUnit.SECONDS);
                pool.shutdownNow();
                end = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("total cost time = " + (end - begin) / 1000);
    }

    /**
     * 一个实现了Callable接口的内部类，该接口可以有返回值，用future可以接收到该返回值
     */
    static final class CallableClass implements Callable<String> {
        private int id;

        public CallableClass(int id) {
            this.id = id;
        }

        public String call() throws Exception {
            System.out.println(Thread.currentThread().getName() + " 开始工作,id : " + id);
            for (int i = 0; i < 10; i++) {
//                System.out.println(Thread.currentThread().getName() + " ： " + i);
            }
//            TimeUnit.SECONDS.sleep(5);
            Random r = new Random();
            long time = (r.nextInt(10) + 1) * 1000;
            Thread.sleep(time);
            //这个返回值返回给future接收
            return "cost time = " + time / 1000 + "s," + Thread.currentThread().getName() + " 完成工作,id : " + id;
        }
    }

    /**
     * 普通线程类
     */
    static final class RunnableInnerClass implements Runnable {
        private int id;

        public RunnableInnerClass(int id) {
            this.id = id;
        }

        public void run() {
            System.out.println(Thread.currentThread().getName() + " 开始工作,id : " + id);
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName() + " ： " + i);
            }
            System.out.println(Thread.currentThread().getName() + " 完成工作,id : " + id);
        }
    }
}
