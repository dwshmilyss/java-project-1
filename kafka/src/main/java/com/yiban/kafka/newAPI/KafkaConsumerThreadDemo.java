package com.yiban.kafka.newAPI;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * kafka consumer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerThreadDemo {

    private static final int NUMPARTITIONS = 9;
    private static final String GROUPNAME = "g3";
    private static final String TOPICNAME = "test1";
    private static final String BROKERS = "10.21.3.129:9092";
    private static final boolean ISAUTOCOMMIT = true;

    public static void main(String[] args) {
        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        System.out.println("KafkaConsumer init completed.....");
        ExecutorService executorService = Executors.newFixedThreadPool(NUMPARTITIONS);
        for (int i = 0; i < NUMPARTITIONS; i++) {
            KafkaConsumerThread kafkaConsumerThread = new KafkaConsumerThread(GROUPNAME, TOPICNAME, i, BROKERS, ISAUTOCOMMIT);
            executorService.execute(kafkaConsumerThread);
        }
        executorService.shutdown();
    }

    static class KafkaConsumerThread implements Runnable {

        private KafkaConsumer<String, String> kafkaConsumer;
        private ReentrantLock lock = new ReentrantLock();


        public KafkaConsumerThread(String groupName, String topicName, int partitionId, String brokers, boolean isAutoCommit) {
            try {
                lock.lock();
                kafkaConsumer = createConsumer(groupName, topicName, partitionId, brokers, isAutoCommit);
            } finally {
                lock.unlock();
            }
        }


        private KafkaConsumer<String, String> createConsumer(String groupName, String topicName, int partitionId, String brokers, boolean isAutoCommit) {
            Properties properties = new Properties();
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//value反系列化方式
            properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, isAutoCommit);//自动提交
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);//指定broker地址，来找到group的coordinator
            properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);//指定用户组

            kafkaConsumer = new KafkaConsumer(properties);
            TopicPartition partition = new TopicPartition(topicName, partitionId);
            kafkaConsumer.assign(Arrays.asList(partition));
            return kafkaConsumer;
        }


        /**
         * 自动提交offset
         */
        public void autoCommit() {
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);//100ms 拉取一次数据
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(Thread.currentThread().getName() + " : topic = " + record.topic() + " partition = " + record.partition() + " offset = " + record.offset() + " key = " + record.key() + " value = " + record.value());
                }
            }
        }


        /**
         * 手动提交offset
         */
        public void manualCommit() {
            long i = 0;
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(100);//100ms 拉取一次数据
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(Thread.currentThread().getName() + " : topic = " + record.topic() + " partition = " + record.partition() + " offset = " + record.offset() + " key = " + record.key() + " value = " + record.value());
                    i++;
                }
                //每100条提交一次
                if (i >= 100) {
                    kafkaConsumer.commitAsync();//手动commit
                    i = 0;
                }
            }
        }

        @Override
        public void run() {
            autoCommit();
        }
    }
}
