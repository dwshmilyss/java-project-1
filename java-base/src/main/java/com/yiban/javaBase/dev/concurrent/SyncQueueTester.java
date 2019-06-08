package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.*;

/**
 * test
 *
 * @auther WEI.DUAN
 * @create 2017/8/19
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class SyncQueueTester {
    private static ExecutorService executor = new ThreadPoolExecutor(1, 1,
            1000, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardPolicy());

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            kickOffEntry(i);

            Thread.sleep(4000);
        }

        executor.shutdown();
    }

    private static void kickOffEntry(final int index) {
        executor.
                submit(
                        new Callable<Void>() {
                            @Override
                            public Void call() throws InterruptedException {
                                System.out.println("start " + index);
                                Thread.sleep(2000); // pretend to do work
                                System.out.println("stop " + index);
                                return null;
                            }
                        }
                );
    }
}
