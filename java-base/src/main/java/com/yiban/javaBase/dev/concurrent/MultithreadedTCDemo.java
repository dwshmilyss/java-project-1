package com.yiban.javaBase.dev.concurrent;

import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;

import java.util.concurrent.LinkedTransferQueue;

/**
 * MultithreadedTC 是一个 Java 库用来测试并发应用。它的主要目的是为了解决并发应用的不确定的问题。你不能控制他们的执行顺序。为了这个目睹，它包含了内部节拍器来控制应用的不同线程的执行顺序。这些测试线程作为类的方法来实现的。
 *
 * @auther WEI.DUAN
 * @create 2017/5/3
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class MultithreadedTCDemo {
    public static void main(String[] args) {
        //9.   创建 ProducerConsumerTest 对象，名为 test。
        ProducerConsumerTest test = new ProducerConsumerTest();
        //10. 使用 TestFramework 类的 runOnce()方法来执行测试。
        System.out.printf("Main: Starting the test\n");
        try {
            TestFramework.runOnce(test);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.printf("Main: The test has finished\n");

    }

    static class ProducerConsumerTest extends MultithreadedTestCase {
        private LinkedTransferQueue<String> queue;

        //3.   实现 initialize() 方法。此方法不接收任何参数，也不返回任何值。它调用父类的 initialize() 方法，然后初始化 queue 属性。
        @Override
        public void initialize() {
            super.initialize();
            queue = new LinkedTransferQueue<String>();
            System.out.printf("Test: The test has been initialized\n");
        }

        //4.   实现 thread1() 方法。它将实现的逻辑是第一个consumer。调用 queue 的 take() 方法，然后把返回值写入操控台。
        public void thread1() throws InterruptedException {
            waitForTick(1);
            String ret = queue.take();
            System.out.printf("Thread 1: %s\n", ret);
        }

        //5.   实现 thread2() 方法。它将实现的逻辑是第二个consumer。首先，使用 waitForTick() 方法，一直等到第一个线程在 take() 方法中进入休眠。然后，调用queue的 take() 方法，并把返回值写入操控台。
        public void thread2() throws InterruptedException {
            waitForTick(1);
            String ret = queue.take();
            System.out.printf("Thread 2: %s\n", ret);
        }

        //6.   实现 thread3() 方法。它将实现的逻辑是producer。 首先，使用 waitForTick() 两次一直等到2个consumers被阻塞。然后，调用 queue的 put() 方法插入2个String 到queue中。
        public void thread3() {
//            waitForTick(1);
//            waitForTick(2);
            queue.put("Event 1");
            queue.put("Event 2");
            System.out.printf("Thread 3: Inserted two elements\n");
        }

        //7.    最后，实现 finish() 方法。写信息到操控台表明测试结束执行。使用assertEquals() 方法检查2个事件已经被consumed（queue的大小为0）。
        public void finish() {
            super.finish();
            System.out.printf("Test: End\n");
            assertEquals(true, queue.size() == 0);
            System.out.printf("Test: Result: The queue is empty\n");
        }

    }
}
