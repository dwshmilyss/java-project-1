package com.yiban.javaBase.dev.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Java 并发 API 提供了一种允许2个并发任务间相互交换数据的同步应用。更具体的说，Exchanger 类允许在2个线程间定义同步点，
 * 当2个线程到达这个点，他们相互交换数据类型，使用第一个线程的数据类型变成第二个的，然后第二个线程的数据类型变成第一个的。
 * 这个类在遇到类似生产者和消费者问题时，是非常有用的。来一个非常经典的并发问题：你有相同的数据buffer，一个或多个数据生产者，和一个或多个数据消费者。
 * 只是Exchange类只能同步2个线程，所以你只能在你的生产者和消费者问题中只有一个生产者和一个消费者时使用这个类。
 *
 * @auther WEI.DUAN
 * @date 2017/5/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ExchangerDemo {

    public static void main(String[] args) {
        // 16. 创建2个buffers。分别给producer和consumer使用.
        List<String> buffer1 = new ArrayList<String>();
        List<String> buffer2 = new ArrayList<String>();

        // 17. 创建Exchanger对象，用来同步producer和consumer。
        Exchanger<List<String>> exchanger = new Exchanger<List<String>>();

        // 18. 创建Producer对象和Consumer对象。
        Producer producer = new Producer(buffer1, exchanger);
        Consumer consumer = new Consumer(buffer2, exchanger);

        // 19. 创建线程来执行producer和consumer并开始线程。
        Thread threadProducer = new Thread(producer);
        Thread threadConsumer = new Thread(consumer);
        threadProducer.start();
        threadConsumer.start();
    }

    /**
     * 1.生产者
     */
    static class Producer implements Runnable {
        // 2. 声明 List<String>对象，名为 buffer。这是等待要被相互交换的数据类型。
        private List<String> buffer;

        // 3. 声明 Exchanger<List<String>>; 对象，名为exchanger。这个 exchanger 对象是用来同步producer和consumer的。
        private Exchanger<List<String>> exchanger;

        // 4. 实现类的构造函数，初始化这2个属性。
        public Producer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        // 5. 实现 run() 方法. 在方法内，实现10次交换。
        @Override
        public void run() {
            int cycle = 1;
            for (int i = 0; i < 10; i++) {
                System.out.printf("Producer: Cycle %d\n", cycle);
                // 6. 在每次循环中，加10个字符串到buffer。
                for (int j = 0; j < 10; j++) {
                    String message = "Event " + ((i * 10) + j);
                    System.out.printf("Producer: %s\n", message);
                    buffer.add(message);
                }
                // 7. 调用 exchange() 方法来与consumer交换数据。此方法可能会抛出InterruptedException 异常, 加上处理代码。
                try {
                    buffer = exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Producer: " + buffer.size());
                cycle++;
            }
        }
    }

    /**
     * 8.定义消费者
     */
    static class Consumer implements Runnable {
        // 9. 声明名为buffer的 List<String>对象。这个对象类型是用来相互交换的。
        private List<String> buffer;
        // 10. 声明一个名为exchanger的 Exchanger<List<String>> 对象。用来同步 producer和consumer。
        private Exchanger<List<String>> exchanger;

        // 11. 实现类的构造函数，并初始化2个属性。
        public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        // 12. 实现 run() 方法。在方法内，实现10次交换。
        @Override
        public void run() {
            int cycle = 1;
            for (int i = 0; i < 10; i++) {
                System.out.printf("Consumer: Cycle %d\n", cycle);

                // 13. 在每次循环，首先调用exchange()方法来与producer同步。Consumer需要消耗数据。此方法可能会抛出InterruptedException异常, 加上处理代码。
                try {
                    buffer = exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 14. 把producer发来的在buffer里的10字符串写到操控台并从buffer内删除，留空。System.out.println("Consumer: " + buffer.size());
                for (int j = 0; j < 10; j++) {
                    String message = buffer.get(0);
                    System.out.println("Consumer: " + message);
                    buffer.remove(0);
                }
                cycle++;
            }
        }
    }

}