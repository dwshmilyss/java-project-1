package com.yiban.javaBase.dev.concurrent.executors;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 执行者控制被拒绝的任务
 * 创建线程池默认的拒绝策略是AbortPolicy 直接抛一个异常出来
 *
 * @auther WEI.DUAN
 * @date 2017/5/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RejectedExecutionDemo {
    public static void main(String[] args) {
        RejectedTaskController controller = new RejectedTaskController();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.setRejectedExecutionHandler(controller);
        System.out.printf("Main: Starting.\n");
        for (int i = 0; i < 3; i++) {
            Task task = new Task("Task" + i);
            executor.submit(task);
        }
        //14.使用shutdown()方法，关闭执行者。

        System.out.printf("Main: Shutting down the Executor.\n");
        executor.shutdown();
        //15.创建其他任务并提交给执行者。

        System.out.printf("Main: Sending another Task.\n");
        Task task = new Task("RejectedTask");
        executor.submit(task);
        //16.写入信息到控制台，表明程序结束。

        System.out.println("Main: End");
        System.out.printf("Main: End.\n");

    }

    //1.创建RejectedTaskController类，实现RejectedExecutionHandler接口。实现这个接口的rejectedExecution()方法。写入被拒绝任务的名称和执行者的名称与状态到控制台。
    static class RejectedTaskController implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.printf("RejectedTaskController: The task %s has been rejected\n", r.toString());
            System.out.printf("RejectedTaskController: %s\n", executor.toString());
            System.out.printf("RejectedTaskController: Terminating:%s\n", executor.isTerminating());
            System.out.printf("RejectedTaksController: Terminated:%s\n", executor.isTerminated());
        }
    }

    private static class Task implements Runnable {
        private String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            System.out.println("Task " + name + ": Starting");
            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("Task %s: ReportGenerator: Generating a report during %d seconds\n", name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Task %s: Ending\n", name);

        }

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
