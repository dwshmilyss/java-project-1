package com.yiban.javaBase.dev.concurrent;

import org.springframework.util.StopWatch;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

/**
 * 线程DEMO
 *
 * @auther WEI.DUAN
 * @create 2017/4/30
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class ThreadDemo {
    public static void main(String[] args) {
//        System.out.println("name = "+Thread.currentThread().getName());
//        System.out.println("id = "+Thread.currentThread().getId());
//        System.out.println("priority = "+Thread.currentThread().getPriority());
//        System.out.println("state = "+Thread.currentThread().getState());

//        testThreadInterrupted();
//        testThreadDeamon();
//        testThreadUnCaughtException();
//        testThreadLocal();

//        testThreadGroup();

//        testJoin();


        TT tt1 = new TT("aa");
//        TT tt2 = new TT("bb");
        tt1.setName("myThread");
        tt1.start();
        try {
            Thread.sleep(60 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        tt2.start();


//        for (int i = 0; i < 10; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println(Thread.currentThread().getName());
//                }
//            }).start();
//        }
    }


    static class TT extends Thread {
        private String str;

        public TT(String str) {
            this.str = str;
        }

        @Override
        public void run() {
            System.out.println(str);
            ExecutorService threadPool = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 50; i++) {
                        System.out.println(Thread.currentThread().getName() + "，第：" + i + "次" + "bb");
                        try {
                            Thread.sleep(1 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void testJoin() {
        class TT extends Thread {
            private String str;

            public TT(String str) {
                this.str = str;
            }

            @Override
            public void run() {
                for (int i = 0; i < 50; i++) {
                    System.out.println(str);
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        TT tt1 = new TT("aa");
        TT tt2 = new TT("bb");
        tt1.start();
        tt2.start();
    }

    /**
     * 测试接收线程的返回结果
     */
    public static void testFuture() {
        ExecutorService threadPool = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());
        final List<Map<String, Integer>> list = new ArrayList<>();
        List<Map<String, Integer>> list1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Future<Map<String, Integer>> res = threadPool.submit(new Callable<Map<String, Integer>>() {
                @Override
                public Map<String, Integer> call() {
                    System.out.println("子线程开始执行任务");
                    Test test = new ThreadDemo().new Test(true);
                    for (int i = 0; i < 10; i++) {
                        test.map.put(Thread.currentThread().getName() + ":" + i, i);
                    }
                    return test.map;
                }
            });


//            Future<Map<String, Integer>> res = threadPool.submit(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("子线程开始执行任务");
//                    Test test = new ThreadDemo().new Test(true);
//                    for (int i = 0; i < 10; i++) {
//                        test.map.put(Thread.currentThread().getName()+":"+i, i);
//                    }
//                    list.add();
//                }
//            },list);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            try {
                System.out.println("主线程在执行任务");
                System.out.println("子线程执行结果为 = " + res.get().size());
                list1.add(res.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        //只有全部执行完毕 才可以关闭线程池
        threadPool.shutdown();

        for (Map<String, Integer> map : list1) {
            for (String key : map.keySet()) {
                System.out.println(key + " = " + map.get(key));
            }
        }
    }

    /**
     * 测试线程组
     */
    public static void testThreadGroup() {
        SearchTask searchTask = new SearchTask();
        ThreadGroup threadGroup = new ThreadGroup("Searcher");
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(threadGroup, searchTask);
            thread.start();
            try {
//                TimeUnit.SECONDS.sleep(2);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(threadGroup.activeCount());
        }
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        for (int i = 0; i < threadGroup.activeCount(); i++) {
            System.out.printf("Thread %s: %s\n", threads[i].getName(), threads[i].getState());
        }
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("========================");
        for (int i = 0; i < threadGroup.activeCount(); i++) {
            System.out.printf("Thread %s: %s\n", threads[i].getName(), threads[i].getState());
        }
    }

    private static void waitFinish(ThreadGroup threadGroup) {
        while (threadGroup.activeCount() > 9) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 测试ThreadLocal
     */
    public static void testThreadLocal() {
        UnsafeTask unsaftTask = new UnsafeTask();
        for (int i = 0; i < 3; i++) {
            Thread task = new Thread(unsaftTask);
            task.start();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试线程中发生未捕获异常的情景
     */
    public static void testThreadUnCaughtException() {
        Task task = new Task();
        Thread thread = new Thread(task);
        /**
         * 首先, 它寻找这个未捕捉的线程对象的异常handle(setUncaughtExceptionHandler)，如果这个handle 不存在，那么JVM会在线程对象的ThreadGroup里寻找非捕捉异常的handler(setDefaultUncaughtExceptionHandler)
         * 如果还不存在，那么 JVM 会寻找默认非捕捉异常handle。
         如果没有一个handler存在, 那么 JVM会把异常的 stack trace 写入操控台并结束任务。
         */
        //静态方法 setDefaultUncaughtExceptionHandler() 为应用里的所有线程对象建立异常 handler
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        //使用 setUncaughtExceptionHandler() 方法设置非检查异常 handler 并开始执行线程。
//        thread.setUncaughtExceptionHandler(new ExceptionHandler());
        thread.start();
    }


    /**
     * 测试守护线程的运行情况
     */
    public static void testThreadDeamon() {
        Deque<WriterTask.Event> deque = new ArrayDeque();
        WriterTask writer = new WriterTask(deque);
        for (int i = 0; i < 3; i++) {
            Thread thread = new Thread(writer);
            thread.start();
        }
        WriterTask.CleanerTask cleaner = new WriterTask.CleanerTask(deque);
        cleaner.start();
    }


    /**
     * 测试线程的中断
     */
    public static void testThreadInterrupted() {
        Thread task = new PrimeGenerator();
        StopWatch watch = new StopWatch();
        task.start();
        watch.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //中断线程 这里是不会抛异常的
        task.interrupt();
        watch.stop();
        System.err.println("time:" + watch.getTotalTimeSeconds());

    }

    /**
     * 测试线程的状态变更
     */
    public static void testThreadStateChanged() {
        Thread threads[] = new Thread[10];
        Thread.State status[] = new Thread.State[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new Calculator(i));
            if ((i % 2) == 0) {
                threads[i].setPriority(Thread.MAX_PRIORITY);
            } else {
                threads[i].setPriority(Thread.MIN_PRIORITY);
            }
            threads[i].setName("Thread " + i);
        }
        try {
            FileWriter file = new FileWriter("./log.txt");
            PrintWriter pw = new PrintWriter(file);
            for (int i = 0; i < 10; i++) {
                pw.println("Main : Status of Thread " + i + " : " + threads[i].getState());
                status[i] = threads[i].getState();
            }
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }
            boolean finish = false;
            while (!finish) {
                for (int i = 0; i < 10; i++) {
                    if (threads[i].getState() != status[i]) {
                        writeThreadInfo(pw, threads[i], status[i]);
                        status[i] = threads[i].getState();
                    }
                }

                finish = true;
                for (int i = 0; i < 10; i++) {
                    finish = finish && (threads[i].getState() == Thread.State.TERMINATED);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeThreadInfo(PrintWriter pw, Thread thread, Thread.State state) {
        pw.printf("Main : Id %d - %s\n", thread.getId(), thread.getName());
        pw.printf("Main : Priority: %d\n", thread.getPriority());
        pw.printf("Main : Old State: %s\n", state);
        pw.printf("Main : New State: %s\n", thread.getState());
        pw.printf("Main : ************************************\n");
    }

    /**
     * 测试线程状态的变更所用到的测试类
     */
    static class Calculator implements Runnable {
        private int number;

        public Calculator(int number) {
            this.number = number;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                System.out.printf("%s: %d * %d = %d\n", Thread.currentThread().getName(), number, i, i * number);
            }
        }
    }

    /**
     * 测试线程中断所用到的测试类
     */
    static class PrimeGenerator extends Thread {
        @Override
        public void run() {
            try {
                doWhile();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }

        private void doWhile() throws InterruptedException {
            long number = 1L;
            while (true) {
                if (isPrime(number)) {
                    System.out.printf("Number %d is Prime\n", number);
                }
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("The Prime Generator has been Interrupted");
                    //即使抛出异常，程序有可能也不会马上中断。上面isPrime的打印可能还会输出一些
                    throw new InterruptedException();
                }
                number++;
            }
        }

        //判断是否是质数
        private boolean isPrime(long number) {
            if (number < 2) return false;
            if (number == 2) return true;
            for (int i = 2; i < number; i++) {
                if (number % i == 0) return false;
            }
            return true;
        }
    }

    /**
     * 测试守护线程
     * WriterTask 任务执行类
     */
    static class WriterTask implements Runnable {
        private Deque<Event> deque;

        public WriterTask(Deque<Event> deque) {
            this.deque = deque;
        }

        @Override
        public void run() {
            for (int i = 1; i < 100; i++) {
                Event event = new Event();
                event.setDate(new Date());
                event.setEvent(String.format("The thread %s has generated an   event", Thread.currentThread().getId()));
                deque.addFirst(event);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        //守护线程类
        private static class CleanerTask extends Thread {
            private Deque<Event> deque;

            public CleanerTask(Deque<Event> deque) {
                this.deque = deque;
                setDaemon(true);
            }

            @Override
            public void run() {
                while (true) {
                    Date date = new Date();
                    clean(date);
                }
            }

            private void clean(Date date) {
                long difference;
                boolean delete;
                if (deque.size() == 0) {
                    return;
                }
                delete = false;
                do {
                    Event e = deque.getLast();
                    difference = date.getTime() - e.getDate().getTime();
                    if (difference > 10000) {
                        System.out.printf("Cleaner: %s\n", e.getEvent());
                        deque.removeLast();
                        delete = true;
                    }
                } while (difference > 10000);
                if (delete) {
                    System.out.printf("Cleaner: Size of the queue: %d\n", deque.size());
                }
            }


        }

        // create a Event javaBean
        private class Event {
            private Date date;
            private String event;

            public Date getDate() {
                return this.date;
            }

            public void setDate(Date date) {
                this.date = date;
            }

            public String getEvent() {
                return this.event;
            }

            public void setEvent(String event) {
                this.event = event;
            }
        }
    }

    /**
     * 测试线程中的未检查异常的捕获
     * 检查异常（Checked exceptions）: 这些异常必须强制捕获它们或在一个方法里的throws子句中。 例如， IOException 或者ClassNotFoundException。
     * 未检查异常（Unchecked exceptions）: 这些异常不用强制捕获它们。例如， NumberFormatException。
     * 首先, 我们必须实现一个类来处理非检查异常。这个类必须实现 UncaughtExceptionHandler 接口并实现在接口内已声明的uncaughtException() 方法。
     * 在这里，命名此类为 ExceptionHandler  ，并让此方法里写有关于抛出异常的线程信息和异常信息
     */
    static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.printf("An exception has been captured\n");
            System.out.printf("Thread'id: %s\n", t.getId());
            System.out.printf("Exception: %s: %s\n", e.getClass().getName(), e.getMessage());
            System.out.printf("Stack Trace: \n");
            e.printStackTrace(System.out);
            System.out.printf("Thread status: %s\n", t.getState());
        }
    }

    /**
     * 尝试在一个线程中发生未捕获的异常
     * 这里试图将一个字符串转为整数
     */
    static class Task implements Runnable {
        @Override
        public void run() {
            int numero = Integer.parseInt("TTT");
        }
    }


    /**
     * 测试ThreadLocal的工具类
     * 本地线程变量为每个使用这些变量的线程储存属性值。可以用 get() 方法读取值和使用 set() 方法改变值。 如果第一次你访问本地线程变量的值，如果没有值给当前的线程对象，那么本地线程变量会调用 initialValue() 方法来设置值给线程并返回初始值。
     * 本地线程类还提供 remove() 方法，删除存储在线程本地变量里的值。
     * Java 并发 API 包括 InheritableThreadLocal 类提供线程创建线程的值的遗传性 。如果线程A有一个本地线程变量，然后它创建了另一个线程B，那么线程B将有与A相同的本地线程变量值。 你可以覆盖 childValue() 方法来初始子线程的本地线程变量的值。 它接收父线程的本地线程变量作为参数。
     */
    static class UnsafeTask implements Runnable {
        //        private Date startDate;
//        private volatile Date startDate;
        private ThreadLocal<Date> startDate = new ThreadLocal() {
            @Override
            protected Date initialValue() {
                return new Date();
            }
        };

        @Override
        public void run() {
//            startDate = new Date();
            System.out.printf("Starting Thread: %s : %s\n", Thread.currentThread().getId(), startDate.get());
            try {
                //Math.rint 四舍五入后保留一位小数 例如 5.0 6.0 st
                TimeUnit.SECONDS.sleep((int) Math.rint(Math.random() * 10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread Finished: %s : %s\n", Thread.currentThread().getId(), startDate.get());

        }
    }

    static class SearchTask implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                System.out.println(Thread.currentThread().getName() + " : " + i);
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class Test {
        private Map<String, Integer> map;

        public Test(boolean flag) {
            if (flag) {
                map = new HashMap<>(16);
            } else {
                map = new LinkedHashMap<>(16);
            }
        }
    }
}
