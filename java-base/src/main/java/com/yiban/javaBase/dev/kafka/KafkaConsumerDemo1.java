package com.yiban.javaBase.dev.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;

/**
 * kafka consumer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerDemo1 {
    private static KafkaConsumer kafkaConsumer;
    static {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.128.129:9092,192.168.128.129:9093,192.168.128.129:9094");
        props.put("group.id", "my-consumer-group-1");
//        props.put("group.id", "my-consumer-group-2");
         //每次最小拉取的消息大小（byte）。Consumer会等待消息积累到一定尺寸后进行批量拉取。默认为1，代表有一条就拉一条
//        props.put("fetch.min.bytes", "1");
//        //每次从单个分区中拉取的消息最大尺寸（byte），默认为1M
//        props.put("max.partition.fetch.bytes", "");
        //是否自动提交已拉取消息的offset。提交offset即视为该消息已经成功被消费，该组下的Consumer无法再拉取到该消息（除非手动修改offset）。默认为true
        props.put("enable.auto.commit", "true");
        //和commit=false 配合使用并没有丢失消息
        props.put("auto.offset.reset", "earliest");
//        props.put("auto.offset.reset", "latest");
//        props.put("enable.auto.commit", "true");
        //自动提交offset的间隔毫秒数，默认5000
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumer = new KafkaConsumer<String, String>(props);

        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        System.out.println("KafkaConsumer init completed.....");
    }

    public static void main(String[] args) {
        String topic = "test1";
        kafkaConsumer.subscribe(Arrays.asList(topic));
        /**
         * 可以指定分区，但是这样kafka就不会负载均衡了
         */
//        TopicPartition partition0 = new TopicPartition(topic, 0);
//        TopicPartition partition1 = new TopicPartition(topic, 1);
//        kafkaConsumer.assign(Arrays.asList(partition0, partition1));
        while(true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(2000);
            for(ConsumerRecord<String, String> record : records) {
                System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value());
            }
            //手动提交offset(manual offset)
//            kafkaConsumer.commitSync();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
