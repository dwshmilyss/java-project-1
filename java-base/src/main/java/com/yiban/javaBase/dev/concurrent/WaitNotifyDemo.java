package com.yiban.javaBase.dev.concurrent;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/10/19
 * wait和notify都是Object的方法，只有在 synchronized 代码块中使用，一般来说调用wait和notify的对象就是synchronized锁住的对象
 * 这里的 (synchronized 和 wait notify) 和  (Lock 和 Condition的await signal)类似
 * Lock -> AQS队列
 * Condition -> ConditionObject队列
 * 而 synchronized 和Lock类似  也有一个对应的等待队列，没有拿到锁的线程就是进入这个队列中等待
 * 而 wait notify就和Condition的await signal 类似，也有一个队列，调用wait就像调用await一样，也会从类似AQS的队列中移动到另一个队列中
 *
 **/
public class WaitNotifyDemo {
    private static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
//        testNotiyfAllBetterThanNotify();
        testProductAndConsume();
    }

    /**
     在这个示例中，有一个Message对象作为共享资源，生产者线程将消息内容设置到Message对象中，而消费者线程从中获取消息内容。
     它们使用wait()和notify()来协调彼此的操作。

     - Producer线程将消息放入共享资源，并通知等待中的消费者线程，然后自己进入等待状态，直到消费者消费完消息。
     - Consumer线程从共享资源中获取消息，并通知等待中的生产者线程，然后自己进入等待状态，直到生产者生产消息。
     **/
    public static void testProductAndConsume() throws InterruptedException {
        final Message message = new Message("Hello, World!");

        Thread producerThread = new Thread(new Producer(message));
        Thread consumerThread = new Thread(new Consumer(message));

        producerThread.start();
        Thread.sleep(1000);
        consumerThread.start();
    }

    /**
     在这个示例中，有5个线程等待共享资源，如果使用notify()，只有一个线程会被唤醒，而其他线程将继续等待，可能导致竞争条件。
     如果你将lock.notify()替换为lock.notifyAll()，所有等待的线程都将被唤醒，确保它们都有机会获得共享资源或执行特定任务，这是更安全的做法。
     **/
    public static void testNotiyfAllBetterThanNotify() {
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new Worker(i));
            thread.start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (lock) {
            // 使用notify()只唤醒一个等待线程，容易造成线程饥饿的情况
//             lock.notify();
            // 使用notifyAll()唤醒所有等待线程
            lock.notifyAll();
        }
    }

    // 为了测试NotifyAll比Notify更好(多个线程竞争资源的情况下，要使用NotifyAll，避免有的线程不能被唤醒)
    static class Worker implements Runnable {
        private int id;

        public Worker(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            synchronized (lock) {
                System.out.println("Worker " + id + " 正在等待...");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Worker " + id + " 已唤醒！");
            }
        }
    }

    static class Message {
        private String content;

        public Message(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    static class Producer implements Runnable {
        private final Message message;

        public Producer(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.println("生产者启动");
            String[] messages = {"Hello", "World", "Goodbye"};

            for (String msg : messages) {
                synchronized (message) {
                    while (message.getContent() != null) {
                        try {
                            System.out.println("生产者阻塞，释放锁");
                            message.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    message.setContent(msg);
                    System.out.println("Producer produced: " + msg);
                    System.out.println("生产者唤醒消费者");
                    message.notify();//只有一个Consumer的时候可以用notify
                    System.out.println("生产者执行完毕");
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private final Message message;

        public Consumer(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.println("消费者启动");
            for (int i = 0; i <= 3; i++) {
                synchronized (message) {
                    while (message.getContent() == null) {
                        try {
                            System.out.println("消费者阻塞，释放锁");
                            message.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String content = message.getContent();
                    System.out.println("Consumer consumed: " + content);
                    message.setContent(null);
                    System.out.println("消费者唤醒生产者");
                    message.notify();//只有一个Producer的时候可以用notify
                    System.out.println("消费者执行完毕");
                }
            }
        }
    }
}
