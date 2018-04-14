package com.yiban.javaBase.dev.reactive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestFuture {
    static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        TestFuture test = new TestFuture();
        test.testTaskRunning("fa", 300);
        //test.testAsyncTaskRunning();
        System.out.println("sssssssssssssssssss");
    }

    public void testTaskRunning(String name, Integer t) {
        System.out.println("prepare for execution: " + name);
        long startTime = System.currentTimeMillis();
        Future<String> fa = executorService.submit(
                () -> {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return String.format("service exec time: %d", t);
                }
        );
        long endTime = System.currentTimeMillis();
        System.out.println("Start execute： " + (endTime - startTime) + "ms");

        try {
            String s = fa.get(); //Future to Blocked
            System.out.println(s);
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis(); //
        System.out.println("End execute： " + (endTime - startTime) + "ms");
    }

    public void testAsyncTaskRunning() {
        System.out.println("Prepare for execution： composite task");
        long startTime = System.currentTimeMillis(); //获取开始时间

        Future<String> fa = executorService.submit(new TimeConsumingService("fa", 200, new String[]{}));
        Future<String> fb = executorService.submit(new TimeConsumingService("fb", 400, new String[]{}));

        System.out.println("Start execute： " + (System.currentTimeMillis() - startTime) + "ms");

        try {
            // What will happen when change line fc and fd ?
            Future<String> fc = executorService.submit(new TimeConsumingService("fc", 400, new String[]{fa.get()}));
            Future<String> fd = executorService.submit(new TimeConsumingService("fd", 200, new String[]{fb.get()}));
            Future<String> fe = executorService.submit(new TimeConsumingService("fe", 200, new String[]{fb.get()}));
            fc.get();
            fd.get();
            fe.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("End execute： " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
