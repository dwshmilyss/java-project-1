package com.yiban.javaBase.test;


import com.yiban.javaBase.dev.concurrent.fork_join.SortTask;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by duanwei on 2017/3/7.
 */
public class Test {
    private static final double SPLIT_SLOP = 1.1;   // 10% slop

    public static void main(String[] args) {

    }

    @org.junit.Test
    public void test(){
        System.out.println("test");
    }

    @org.junit.Test
    public void run() throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Random rnd = new Random();
        int SIZE = 10000;
        long[] array = new long[SIZE];
        for (int i = 0; i < SIZE; i++) {
            array[i] = rnd.nextInt();
        }
        forkJoinPool.submit(new SortTask(array));

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1000, TimeUnit.SECONDS);

        for (int i = 1; i < SIZE; i++) {
            assertTrue(array[i - 1] < array[i]);
        }
    }
}
