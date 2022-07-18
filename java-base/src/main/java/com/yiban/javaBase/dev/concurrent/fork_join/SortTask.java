package com.yiban.javaBase.dev.concurrent.fork_join;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

/**
 * fork join 对数组排序
 *
 * @auther WEI.DUAN
 * @date 2017/4/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SortTask extends RecursiveAction {
    private static final long serialVersionUID = 5041101410538872038L;

    final long[] array;
    final int start;
    final int end;
    private int THRESHOLD = 100; //For demo only

    public SortTask(long[] array) {
        this.array = array;
        this.start = 0;
        this.end = array.length - 1;
    }

    public SortTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    public static void main(String[] args) throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Random rnd = new Random();
        int SIZE = 10000;
        long[] array = new long[SIZE];
        for (int i = 0; i < SIZE; i++) {
            array[i] = rnd.nextInt(SIZE);
        }
        forkJoinPool.submit(new SortTask(array));

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1000, TimeUnit.SECONDS);

        for (int i = 1; i < SIZE; i++) {
            System.out.println(array[i]);
        }
    }

    protected void compute() {
        if (end - start < THRESHOLD)
            sequentiallySort(array, start, end);
        else {
            int pivot = partition(array, start, end);
            new SortTask(array, start, pivot - 1).fork();
            new SortTask(array, pivot + 1, end).fork();
        }
    }

    private int partition(long[] array, int start, int end) {
        long x = array[end];
        int i = start - 1;
        for (int j = start; j < end; j++) {
            if (array[j] <= x) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, end);
        return i + 1;
    }

    private void swap(long[] array, int i, int j) {
        if (i != j) {
            long temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private void sequentiallySort(long[] array, int lo, int hi) {
        Arrays.sort(array, lo, hi + 1);
    }

}
