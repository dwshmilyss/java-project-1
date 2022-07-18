package com.yiban.kafka.kafka_0_11.dev;


import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * kafka producer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaProducerDemo1 {

    //0.11.0.3版本
    private static KafkaProducer kafkaProducer;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(KafkaProducerDemo1.class);
    public static final String BROKERS = "10.21.3.74:9092,10.21.3.75:9092,10.21.3.76:9092,10.21.3.77:9092";
    public static final String TOPIC = "test_8_3";


    static {
        Properties props = new Properties();
//        props.put("bootstrap.servers", "192.168.128.129:9092,192.168.128.129:9093,192.168.128.129:9094");
        props.put("bootstrap.servers", BROKERS);
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer(props);

        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        System.out.println("init completed.....");
    }


    public static void main(String[] args) {

    }



    /**
     * 需要:
     * 1、设置transactional.id
     * 2、设置enable.idempotence
     * @return
     */
    private Producer buildProducer() {
        // create instance for properties to access producer configs
        Properties props = new Properties();
        // bootstrap.servers是Kafka集群的IP地址。多个时,使用逗号隔开
        props.put("bootstrap.servers", "localhost:9092");
        // 设置事务id
        props.put("transactional.id", "first-transactional");
        // 设置幂等性
        props.put("enable.idempotence",true);
        //Set acknowledgements for producer requests.
        props.put("acks", "all");
        //If the request fails, the producer can automatically retry,
        props.put("retries", 1);
        //Specify buffer size in config,这里不进行设置这个属性,如果设置了,还需要执行producer.flush()来把缓存中消息发送出去
        //props.put("batch.size", 16384);
        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);
        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);
        // Kafka消息是以键值对的形式发送,需要设置key和value类型序列化器
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;
    }

    /**
     * 在一个事务只有生产消息操作
     */
    public void onlyProduceInTransaction() {
        Producer producer = buildProducer();
        // 1.初始化事务
        producer.initTransactions();
        // 2.开启事务
        producer.beginTransaction();

        try {
            // 3.kafka写操作集合
            // 3.1 do业务逻辑
            // 3.2 发送消息
            producer.send(new ProducerRecord<String, String>("test", "transaction-data-1"));
            producer.send(new ProducerRecord<String, String>("test", "transaction-data-2"));
            // 3.3 do其他业务逻辑,还可以发送其他topic的消息。

            // 4.事务提交
            producer.commitTransaction();

        } catch (Exception e) {
            // 5.放弃事务
            producer.abortTransaction();
        }
    }


    /**
     * 在一个事务内,即有生产消息又有消费消息，即常说的Consume-tansform-produce模式
     */
    public void consumeTransferProduce() {
        // 1.构建生产者
        Producer producer = buildProducer();
        // 2.初始化事务(生成productId),对于一个生产者,只能执行一次初始化事务操作
        producer.initTransactions();
        // 3.构建消费者和订阅主题
        Consumer consumer = buildConsumer();
        consumer.subscribe(Arrays.asList("test"));
        while (true) {
            // 4.开启事务
            producer.beginTransaction();
            // 5.1 接受消息
            ConsumerRecords<String, String> records = consumer.poll(500);
            try {
                // 5.2 do业务逻辑;
                System.out.println("customer Message---");
                Map<TopicPartition, OffsetAndMetadata> commits = Maps.newHashMap();
                for (ConsumerRecord<String, String> record : records) {
                    // 5.2.1 读取消息,并处理消息。print the offset,key and value for the consumer records.
                    System.out.printf("offset = %d, key = %s, value = %s\n",
                            record.offset(), record.key(), record.value());

                    // 5.2.2 记录提交的偏移量
                    commits.put(new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset()));

                    // 6.生产新的消息。比如外卖订单状态的消息,如果订单成功,则需要发送给商家结转消息或者派送员的提成消息
                    producer.send(new ProducerRecord<String, String>("test", "data2"));
                }

                // 7.提交偏移量
                producer.sendOffsetsToTransaction(commits, "group0323");

                // 8.事务提交
                producer.commitTransaction();

            } catch (Exception e) {
                // 7.放弃事务
                producer.abortTransaction();
            }
        }
    }
    /**
     * 需要:
     * 1、关闭自动提交 enable.auto.commit
     * 2、isolation.level为read_committed
     * 而且在代码里面也不能使用手动提交commitSync( )或者commitAsync( )
     * @return
     */
    public Consumer buildConsumer() {
        Properties props = new Properties();
        // bootstrap.servers是Kafka集群的IP地址。多个时,使用逗号隔开
        props.put("bootstrap.servers", "localhost:9092");
        // 消费者群组
        props.put("group.id", "group0323");
        // 设置隔离级别
        props.put("isolation.level","read_committed");
        // 关闭自动提交
        props.put("enable.auto.commit", "false");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer
                <String, String>(props);
        return consumer;
    }


    /**
     * 在一个事务只有消费消息操作
     * 这种操作其实没有什么意义，跟使用手动提交效果一样，无法保证消费消息操作和提交偏移量操作在一个事务。
     */
    public void onlyConsumeInTransaction() {
        Producer producer = buildProducer();
        // 1.初始化事务
        producer.initTransactions();
        // 2.开启事务
        producer.beginTransaction();
        // 3.kafka读消息的操作集合
        Consumer consumer = buildConsumer();
        while (true) {
            // 3.1 接受消息
            ConsumerRecords<String, String> records = consumer.poll(500);

            try {
                // 3.2 do业务逻辑;
                System.out.println("customer Message---");
                Map<TopicPartition, OffsetAndMetadata> commits = Maps.newHashMap();
                for (ConsumerRecord<String, String> record : records) {
                    // 3.2.1 处理消息 print the offset,key and value for the consumer records.
                    System.out.printf("offset = %d, key = %s, value = %s\n",
                            record.offset(), record.key(), record.value());

                    // 3.2.2 记录提交偏移量
                    commits.put(new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset()));
                }

                // 4.提交偏移量
                producer.sendOffsetsToTransaction(commits, "group0323");

                // 5.事务提交
                producer.commitTransaction();

            } catch (Exception e) {
                // 6.放弃事务
                producer.abortTransaction();
            }
        }

    }

}
