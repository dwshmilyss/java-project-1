package com.yiban.kafka.kafka_0_10.api.producer;


import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * kafka producer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaProducerDemo1 {

    //0.10.1.0版本
    private static KafkaProducer kafkaProducer;

    private static final Logger logger = Logger.getLogger(KafkaProducerDemo1.class);
    public static final String BROKERS = "10.21.3.74:9092,10.21.3.75:9092,10.21.3.76:9092,10.21.3.77:9092";
    public static final String TOPIC = "test_8_3";


    static {
        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        Properties props = new Properties();
//        props.put("bootstrap.servers", "192.168.128.129:9092,192.168.128.129:9093,192.168.128.129:9094");
        props.put("bootstrap.servers", BROKERS);
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer(props);


        System.out.println("init completed.....");
    }


    public static void main(String[] args) {
        try {
            int i = 1;
            while (true) {
                System.out.println("send begin.....");

//                ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, String.valueOf(i), "this is message : " + i);
                //测试key为null时会怎么分配partition(平均分配到每个partition 也就是轮询)
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, null, "this is message : " + i);

                kafkaProducer.send(record, new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        System.out.println("callback .....");
                        if (e != null) {
                            e.printStackTrace();
                        }
                        System.out.println("topic = " + metadata.topic() + ",partition = " + metadata.partition() + ",offset = " + metadata.offset() + ",timestamp = " + metadata.timestamp());
                    }
                });

                i++;
                if (i % 1000 == 0) {
                    Thread.sleep(1000);
                }

                if (i == 10000) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
