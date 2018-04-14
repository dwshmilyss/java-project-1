package com.yiban.javaBase.dev.concurrent.fork_join;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * 利用fork join进行数值求和
 *
 * @auther WEI.DUAN
 * @date 2017/4/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountTask extends RecursiveTask<Integer> {

    public static final int threshold = 2;
    private static final long serialVersionUID = -7694721454271199763L;
    private int start;
    private int end;

    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static void main(String[] args) {
        ForkJoinPool forkjoinPool = new ForkJoinPool();

        //生成一个计算任务，计算1+2+3+4
        CountTask task = new CountTask(1, 100);
        //同步执行任务
//        Future<Integer> result = forkjoinPool.submit(task);
        int res = forkjoinPool.invoke(task);
        //异步执行任务
//        forkjoinPool.execute(task);
//        int res = task.join();
        //阻塞3秒
        try {
            forkjoinPool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("the result is %s\n", res);
        forkjoinPool.shutdown();
        System.out.println("线程池关闭");
//        try {
//            System.out.println(result.get());
//        } catch (Exception e) {
//            System.out.println(e);
//        }
    }

    @Override
    protected Integer compute() {
        int sum = 0;

        //如果任务足够小就计算任务
        boolean canCompute = (end - start) <= threshold;
        if (canCompute) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end) / 2;
            CountTask leftTask = new CountTask(start, middle);
            CountTask rightTask = new CountTask(middle + 1, end);

            invokeAll(leftTask, rightTask);
//            // 执行子任务 等价于上面的invokeAll
//            leftTask.fork();
//            rightTask.fork();
//
//            //等待任务执行结束合并其结果
            int leftResult = leftTask.join();
            int rightResult = rightTask.join();

            //合并子任务
            sum = leftResult + rightResult;

        }
//        try {
//            TimeUnit.SECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return sum;
    }
}
