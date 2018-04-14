package com.yiban.javaBase.dev.concurrent.executors;

import java.util.concurrent.*;

/**
 * FutureTask类提供一个done()方法，允许你在执行者执行任务完成后执行一些代码。你可以用来做一些后处理操作，生成一个报告，通过e-mail发送结果，或释放一些资源。
 * 当执行的任务由FutureTask来控制完成，FutureTask会内部调用这个方法。这个方法在任务的结果设置和它的状态变成isDone状态之后被调用，
 * 不管任务是否已经被取消或正常完成。  默认情况下，这个方法是空的。你可以重写FutureTask类实现这个方法来改变这种行为。
 *
 * @auther WEI.DUAN
 * @date 2017/5/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DoneTaskDemo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ResultTask[] resultTasks = new ResultTask[5];
        for (int i = 0; i < 5; i++) {
            ExecutableTask executableTask = new ExecutableTask("Task " + i);
            resultTasks[i] = new ResultTask(executableTask);
            executorService.submit(resultTasks[i]);
        }
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < resultTasks.length; i++) {
            resultTasks[i].cancel(true);
        }

        for (int i = 0; i < resultTasks.length; i++) {
            try {
                if (!resultTasks[i].isCancelled()) {
                    //这里调用resultTasks[i].get()获取到的值 其实就是ExecutableTask.call()的返回值
                    System.out.printf("%s\n", resultTasks[i].get());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();

    }

    static class ExecutableTask implements Callable<String> {
        private String name;

        public ExecutableTask(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String call() throws Exception {
            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
            }
            return "Hello, world. I'm " + name;

        }
    }

    /**
     * ResultTask作为一个FutureTask类的子类用来控制ExecutableTask对象的执行
     */
    static class ResultTask extends FutureTask<String> {

        private String name;

        public ResultTask(Callable<String> callable) {
            super(callable);
            this.name = ((ExecutableTask) callable).getName();
        }

        //在建立返回值和改变任务的状态为isDone状态后，done()方法被FutureTask类内部调用。你不能改变任务的结果值和它的状态，但你可以关闭任务使用的资源，写日志消息，或发送通知。
        @Override
        protected void done() {
            if (isCancelled()) {
                System.out.printf("%s: Has been canceled\n", name);
            } else {
                System.out.printf("%s: Has finished\n", name);
            }
        }
    }
}
