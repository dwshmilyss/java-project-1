package com.yiban.kafka.kafka_0_10.api.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * kafka consumer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerThreadDemo {

    private static final int NUMPARTITIONS = 8;
    private static final String GROUPNAME = "test_1_1_g2";
    private static final String TOPICNAME = "test_1_1";
//    private static final String BROKERS = "10.21.3.74:9092,10.21.3.75:9092,10.21.3.76:9092,10.21.3.77:9092";
    private static final String BROKERS = "192.168.200.128:9092";
    private static final boolean ISAUTOCOMMIT = false;
    private static final boolean ISHIGH = true;

    public static void main(String[] args) {
        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        System.out.println("KafkaConsumer init completed.....");
        ExecutorService executorService = Executors.newFixedThreadPool(NUMPARTITIONS);
        for (int i = 0; i < NUMPARTITIONS; i++) {
            //这里可以设置一个阻塞队列，每次poll到的数据 先放入阻塞队列，如果阻塞队列满了，就暂停poll
            LinkedBlockingQueue<ConsumerRecord<String, String>> linkedBlockingQueue = new LinkedBlockingQueue<>(50);
            KafkaConsumerThread kafkaConsumerThread = new KafkaConsumerThread(GROUPNAME, TOPICNAME, i, BROKERS, ISAUTOCOMMIT, ISHIGH, linkedBlockingQueue);
            executorService.execute(kafkaConsumerThread);
        }
        executorService.shutdown();
    }

    /**
     * 消费者的多线程处理模型
     * Kafka的Consumer的接口为非线程安全的。多线程共用IO，Consumer线程需要自己做好线程同步。如果想立即终止consumer，唯一办法是用调用接口：wakeup()，使处理线程产生WakeupException
     */
    static class KafkaConsumerThread implements Runnable {
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private KafkaConsumer<String, String> kafkaConsumer;
        private ReentrantLock lock = new ReentrantLock();
        private boolean isAutoCommit;
        private String groupName;
        private String topicName;
        private int partitionId;
        private String brokers;
        private boolean isHigh;
        LinkedBlockingQueue<ConsumerRecord<String, String>> linkedBlockingQueue = null;

        public KafkaConsumerThread(String groupName, String topicName, int partitionId, String brokers, boolean isAutoCommit, boolean isHigh, LinkedBlockingQueue<ConsumerRecord<String, String>> linkedBlockingQueue) {
            try {
                lock.lock();
                this.isAutoCommit = isAutoCommit;
                this.groupName = groupName;
                this.topicName = topicName;
                this.partitionId = partitionId;
                this.brokers = brokers;
                this.isHigh = isHigh;
                this.linkedBlockingQueue = linkedBlockingQueue;
                createConsumer(groupName, topicName, partitionId, brokers, isHigh);
            } finally {
                lock.unlock();
            }
        }

        /**
         * 创建consumer
         *
         * @param groupName   消费者组
         * @param topicName
         * @param partitionId
         * @param brokers
         * @param isHigh      是否使用高级API
         * @return
         */
        private KafkaConsumer<String, String> createConsumer(String groupName, String topicName, int partitionId, String brokers, boolean isHigh) {
            Properties properties = new Properties();
            //key反序列化方式
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            //value反系列化方式
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            //自动提交
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, isAutoCommit);
            //指定broker地址，来找到group的coordinator
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            //指定用户组
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
            //从最早开始消费 earliest latest
            properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            //broker检测consumer是否超时 如果在这段时间内consumer没有发送心跳给broker，则认为consumer已经离线，会触发rebalance
            properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
            // 心跳间隔设置 通常要小于session.timeout.ms 但也不能高于session.timeout.ms的1/3
            properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);
            //
            properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 31000);

            properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 1000);
            //控制一次poll最多可以拉取多少条数据
            properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

            kafkaConsumer = new KafkaConsumer(properties);
            return kafkaConsumer;
        }


        @Override
        public void run() {
            TopicPartition partition = null;
            try {
                if (isHigh) {
                    //创建High API的consumer
                    kafkaConsumer.subscribe(Arrays.asList(topicName));
                    autoCommit(!closed.get());
                } else {
                    //创建low API的consumer
                    partition = new TopicPartition(topicName, partitionId);
                    kafkaConsumer.assign(Arrays.asList(partition));
                    if (isAutoCommit) {
                        autoCommit(!closed.get());
                    } else {
                        try {
                            manualCommit(!closed.get(), partition);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (WakeupException e) {
                // Ignore exception if closing
                if (!closed.get()) {
                    throw e;
                }
            } finally {
                kafkaConsumer.close();
            }
        }

        /**
         * 自动提交offset
         */
        public void autoCommit(boolean isClosed) {
            while (isClosed) {
                /**
                 *
                 */
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);//100ms 拉取一次数据
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(Thread.currentThread().getName() + " : topic = " + record.topic() + " partition = " + record.partition() + " offset = " + record.offset() + " key = " + record.key() + " value = " + record.value());
                }

//                TopicPartition topicPartition0 = new TopicPartition(topicName,0);
//                TopicPartition topicPartition1 = new TopicPartition(topicName,1);
//                TopicPartition topicPartition2 = new TopicPartition(topicName,2);
//                TopicPartition topicPartition3 = new TopicPartition(topicName,3);
//                TopicPartition topicPartition4 = new TopicPartition(topicName,4);
//                TopicPartition topicPartition5 = new TopicPartition(topicName,5);
//                TopicPartition topicPartition6 = new TopicPartition(topicName,6);
//                TopicPartition topicPartition7 = new TopicPartition(topicName,7);
//                List<TopicPartition> topicPartitionList = Arrays.asList(topicPartition0, topicPartition1,topicPartition2,topicPartition3,topicPartition4,topicPartition5,topicPartition6,topicPartition7);
//                kafkaConsumer.pause(topicPartitionList);
//
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                kafkaConsumer.resume(topicPartitionList);
                /**
                 * 遍历所有分区 这里得遍历所有分区，否则还是只消费了一个区：(待验证)
                 */
//                for (TopicPartition topicPartition : records.partitions()) {
//                    List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
//                    for (ConsumerRecord<String,String> record : partitionRecords) {
//                        System.out.println(
//                                "message==>key:" + record.key() + " value:" + record.value() + " offset:" + record.offset()
//                                        + " 分区:" + record.partition());
//                    }
//                }
            }
        }


        /**
         * 手动提交offset
         */
        public void manualCommit(boolean isClosed, TopicPartition partition) throws InterruptedException {
            commit(isClosed, partition); //
        }

        private void commit(boolean isClosed, TopicPartition partition) throws InterruptedException {
            int i = 0;
            boolean flag = true;
            while (isClosed) {
                long start = System.currentTimeMillis();
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);//1000ms 拉取一次数据
                System.out.println("poll records size = " + records.count());
//                System.out.println("poll cost = " + (int)((System.currentTimeMillis() - start) / 1000));
                /*
                 * max.poll.interval.ms 默认300秒
                 * 从poll完成后一直到下次循环开始 这个处理业务逻辑的时间间隔就是max.poll.interval.ms
                 * 处理完成后 会进行下一次的poll
                 * 处理业务逻辑的时间不能超过这个配置 否则consumer coordinator会让这个消费者离线
                 * 并重新进行rebalance
                 */
                for (ConsumerRecord<String, String> record : records) {
                    //拿到数据后先放入队列里
                    flag = linkedBlockingQueue.offer(record);
                    //如果满了就暂停poll kafka
                    i++;
                    if (!flag) {
                        System.out.println("队列满了 暂停拉取");
                        kafkaConsumer.pause(Arrays.asList(partition));
                        break;
                    }
                }
                for (int j = 0; j < i; j++) {
                    ConsumerRecord<String, String> record1 = linkedBlockingQueue.poll();
                    if (record1 != null) {
                        //TODO 处理逻辑
                        System.out.println(Thread.currentThread().getName() + " : topic = " + record1.topic() + " partition = " + record1.partition() + " offset = " + record1.offset() + " key = " + record1.key() + " value = " + record1.value());
                        Thread.sleep(1000);
                    }
                }
                if (!flag) {//如果这时发现暂停了，则可以重新开始拉取kafka的数据
                    System.out.println("队列有位置了 继续拉取");
                    kafkaConsumer.resume(Arrays.asList(partition));
                }


//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //每100条提交一次
//                if (i >= 100) {
//                    kafkaConsumer.commitSync();//同步commit
//                    i = 0;
//                }

            }
        }

        /**
         * 用lambda简化代码
         *
         * @param isSync 是否异步提交（未实现，扩展时应传入该参数）
         */
        private void commitWithLambda(boolean isSync) {
            final int[] counter = new int[1];
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);//100ms 拉取一次数据
                //jdk1.8的写法(lambda)
                records.forEach(record -> {
                    System.out.printf("thread : %s , topic: %s , partition: %d , offset = %d, key = %s, value = %s%n", Thread.currentThread().getName(), record.topic(),
                            record.partition(), record.offset(), record.key(), record.value());
                    counter[0]++;
                });
                if (counter[0] >= 100) {
                    //异步提交，可以注册回调函数
                    kafkaConsumer.commitAsync((Map<TopicPartition, OffsetAndMetadata> offsets, Exception e) -> {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        offsets.forEach((topicPartition, offsetAndMetadata) -> {
                            System.out.printf("thread : %s , topic: %s , partition: %d , offset = %s%n", Thread.currentThread().getName(), topicPartition.topic(), topicPartition.partition(), offsetAndMetadata.toString());
                        });
                    });
                    counter[0] = 0;
                }
            }
        }

        /**
         * 提交一个partition（同步和异步都可以实现）
         */
        private void commitByPartition() {
            AtomicLong atomicLong = new AtomicLong();
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
                records.partitions().forEach(topicPartition -> {
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
                    partitionRecords.forEach(record -> {
                        System.out.printf("thread : %s , topic: %s , partition: %d , offset = %d, key = %s, value = %s%n", Thread.currentThread().getName(), record.topic(),
                                record.partition(), record.offset(), record.key(), record.value());
                    });
                    long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                    //提交一个partition，调用commitSync时，需要添加最后一条消息的偏移量
                    kafkaConsumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(lastOffset + 1)));
                });
            }
        }

        // Shutdown hook which can be called from a separate thread
        public void shutdown() {
            closed.set(true);
            kafkaConsumer.wakeup();
        }

    }
}
