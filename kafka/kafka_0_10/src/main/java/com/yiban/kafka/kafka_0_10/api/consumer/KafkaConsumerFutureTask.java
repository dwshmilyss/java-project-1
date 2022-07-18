package com.yiban.kafka.kafka_0_10.api.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @auther WEI.DUAN
 * @date 2019/6/21
 * @website http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerFutureTask {

    /**
     * Logger
     **/
    private static final Logger LOGGER = Logger.getLogger(KafkaConsumerFutureTask.class);
    private static final int NUMPARTITIONS = 8;
    private static final String GROUPNAME = "test_8_3_g2";
    private static final String TOPICNAME = "test_8_3";
    private static final String BROKERS = "10.21.3.74:9092,10.21.3.75:9092,10.21.3.76:9092,10.21.3.77:9092";

    public static void main(String[] args) {
        org.apache.log4j.Logger.getLogger("org").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("akka").setLevel(Level.WARN);
        org.apache.log4j.Logger.getLogger("kafka").setLevel(Level.WARN);
        KafkaConsumer<String, String> consumer;
        Properties props = new Properties();
        props.put("bootstrap.servers", BROKERS);
        props.put("group.id", GROUPNAME);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", false);
        props.put("session.timeout.ms", 30000);
        props.put("heartbeat.interval.ms", 10000);
        props.put("request.timeout.ms", 31000);
        props.put("max.partition.fetch.bytes", 1000);

        ExecutorService executorServiceForAsyncKafkaEventProcessing = Executors.newFixedThreadPool(NUMPARTITIONS);

        for (int i = 0; i < NUMPARTITIONS; i++) {
            Boolean isPassed = true;
            try {
                ConsumeEventInThread consumerEventInThread = new ConsumeEventInThread(props);
                FutureTask<Boolean> futureTask = new FutureTask<Boolean>(consumerEventInThread);
                executorServiceForAsyncKafkaEventProcessing.execute(futureTask);
                try {
                    isPassed = (Boolean) futureTask.get(Long.parseLong(props.getProperty("session.timeout.ms")) - Long.parseLong("5000"), TimeUnit.MILLISECONDS);
                } catch (Exception Exception) {
                    LOGGER.warn("Time out after waiting till session time out");
                }
//                    consumer.commitSync();
                LOGGER.info("Successfully committed offset for topic " + Arrays.asList(props.getProperty("kafka.consumer.topic.name")));

            } catch (Exception e) {
                LOGGER.error("Unhandled exception in while consuming messages " + Arrays.asList(props.getProperty("kafka.consumer.topic.name")), e);
            }
        }
        executorServiceForAsyncKafkaEventProcessing.shutdown();
//            Boolean isPassed = true;
//            try {
//                ConsumerRecords<String, String> records = consumer.poll(1000);
//                if (records.count() > 0) {
//                    ConsumeEventInThread consumerEventInThread = new ConsumeEventInThread(records);
//                    FutureTask<Boolean> futureTask = new FutureTask<Boolean>(consumerEventInThread);
//                    executorServiceForAsyncKafkaEventProcessing.execute(futureTask);
//                    try {
//                        isPassed = (Boolean) futureTask.get(Long.parseLong(props.getProperty("session.timeout.ms")) - Long.parseLong("5000"), TimeUnit.MILLISECONDS);
//                    } catch (Exception Exception) {
//                        LOGGER.warn("Time out after waiting till session time out");
//                    }
////                    consumer.commitSync();
//                    LOGGER.info("Successfully committed offset for topic " + Arrays.asList(props.getProperty("kafka.consumer.topic.name")));
//                } else {
//                    LOGGER.info("Failed to process consumed messages, will not Commit and consume again");
//                }
//
//            } catch (Exception e) {
//                LOGGER.error("Unhandled exception in while consuming messages " + Arrays.asList(props.getProperty("kafka.consumer.topic.name")), e);
//            }
//        }
    }

    private static class ConsumeEventInThread implements Callable {

        private KafkaConsumer consumer;
        private ReentrantLock lock = new ReentrantLock();

        public ConsumeEventInThread(Properties props) {
            try {
                lock.lock();
                consumer = new KafkaConsumer(props);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public Object call() throws Exception {
            consumer.subscribe(Arrays.asList(TOPICNAME));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(1000);

                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(Thread.currentThread().getName() + " : topic = " + record.topic() + " partition = " + record.partition() + " offset = " + record.offset() + " key = " + record.key() + " value = " + record.value());
                }


//            for (TopicPartition topicPartition : records.partitions()) {
//                List<ConsumerRecord<String, String>> partitionRecords = records.records(topicPartition);
//                for (ConsumerRecord<String, String> record : partitionRecords) {
//                    System.out.println(Thread.currentThread().getName() + " :  " +
//                            "message==>key:" + record.key() + " value:" + record.value() + " offset:" + record.offset()
//                            + " 分区:" + record.partition());
//                }
//            }
                return null;
            }
        }
    }
}