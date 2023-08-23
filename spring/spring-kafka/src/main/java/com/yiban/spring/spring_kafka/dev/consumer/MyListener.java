
package com.yiban.spring.spring_kafka.dev.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * Simple to Introduction
 * className: MyListener
 *
 * @author EricYang
 * @version 2018/5/12 18:33
 */

@Slf4j
public class MyListener {
    private static final String TPOIC = "test_1_1";

    @KafkaListener(id = "id0",groupId = "test_8_3_g1",topics = {"test_8_3"} )
    public void listenPartition0(List<ConsumerRecord<?, ?>> records) {
        log.info("Id0 Listener, Thread ID: " + Thread.currentThread().getId());
        log.info("Id0 records size " +  records.size());

        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("p0 Received message={}",  message);
            }
        }
    }

//    @KafkaListener(id = "id0", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "0" }) })
//    public void listenPartition0(List<ConsumerRecord<?, ?>> records) {
//        log.i nfo("Id0 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id0 records size " +  records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p0 Received message={}",  message);
//            }
//        }
//    }
//
//    @KafkaListener(id = "id1", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "1" }) })
//    public void listenPartition1(List<ConsumerRecord<?, ?>> records) {
//        log.info("Id1 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id1 records size " +  records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p1 Received message={}",  message);
//            }
//        }
//    }
//
//    @KafkaListener(id = "id2", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "2" }) })
//    public void listenPartition2(List<ConsumerRecord<?, ?>> records) {
//        log.info("Id2 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id2 records size " +  records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p2 Received message={}",  message);
//            }
//        }
//    }
//
//    @KafkaListener(id = "id3", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "3" }) })
//    public void listenPartition3(List<ConsumerRecord<?, ?>> records) {
//        log.info("Id3 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id3 records size " + records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p3 Received message={}", message);
//            }
//        }
//    }
//
//    @KafkaListener(id = "id4", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "4" }) })
//    public void listenPartition4(List<ConsumerRecord<?, ?>> records) {
//        log.info("Id4 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id4 records size " + records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p4 Received message={}", message);
//            }
//        }
//    }
//
//    @KafkaListener(id = "id5", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "5" }) })
//    public void listenPartition5(List<ConsumerRecord<?, ?>> records) {
//        log.info("Id5 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id5 records size " + records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p5 Received message={}", message);
//            }
//        }
//    }
//
//    @KafkaListener(id = "id6", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "6" }) })
//    public void listenPartition6(List<ConsumerRecord<?, ?>> records) {
//        log.info("Id6 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id6 records size " + records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p6 Received message={}", message);
//            }
//        }
//    }
//
//    @KafkaListener(id = "id7", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "7" }) })
//    public void listenPartition7(List<ConsumerRecord<?, ?>> records,Acknowledgment acknowledgment) {
//        log.info("Id7 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id7 records size " + records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p7 Received message={}", message);
//            }
//        }
//        acknowledgment.acknowledge();
//    }
//
//
//    /**
//     * 这个方法只是测试手动提交offset
//     * @param records
//     * @param acknowledgment
//     */
//    @KafkaListener(id = "id8", topicPartitions = { @TopicPartition(topic = TPOIC, partitions = { "8" }) })
//    public void listenPartition8(List<ConsumerRecord<?, ?>> records,Acknowledgment acknowledgment) {
//        log.info("Id8 Listener, Thread ID: " + Thread.currentThread().getId());
//        log.info("Id8 records size " + records.size());
//
//        for (ConsumerRecord<?, ?> record : records) {
//            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//            log.info("Received: " + record);
//            if (kafkaMessage.isPresent()) {
//                Object message = record.value();
//                String topic = record.topic();
//                log.info("p8 Received message={}", message);
//            }
//        }
//        try {
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }finally {
//            log.info("start commit offset");
//            acknowledgment.acknowledge();//手动提交偏移量
//            log.info("stop commit offset");
//        }
//    }
}
