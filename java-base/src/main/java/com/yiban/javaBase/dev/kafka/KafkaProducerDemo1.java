package com.yiban.javaBase.dev.kafka;


import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * kafka producer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaProducerDemo1 {

    //0.10.2.0版本
    private static KafkaProducer kafkaProducer;

    //0.8.2.1版本
    private static Producer kafkaProducer0820;


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(KafkaProducerDemo1.class);

    static {
        Properties props = new Properties();
//        props.put("bootstrap.servers", "192.168.128.129:9092,192.168.128.129:9093,192.168.128.129:9094");
        props.put("bootstrap.servers", "10.21.3.129:9092");
        props.put("acks", "1");
        props.put("retries", 0);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        kafkaProducer = new KafkaProducer(props);

        Properties props0820 = new Properties();
        props0820.put("zookeeper.connect", "10.21.3.129:2181");
        props0820.put("metadata.broker.list", "10.21.3.129:9092");
        props0820.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props0820.put("serializer.class", "kafka.serializer.StringEncoder");
        props0820.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props0820.put("request.required.acks", "1");
        ProducerConfig producerConfig = new ProducerConfig(props0820);
        kafkaProducer0820 = new Producer(producerConfig);

        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        System.out.println("init completed.....");
    }


    public static void main(String[] args) {
        try {
            String topic = "test1";
            int i = 0;
            while (true){
                System.out.println("send begin.....");

                ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, String.valueOf(i),"this is message : " + i);
                kafkaProducer.send(record, new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        System.out.println("callback .....");
                        if (e != null)
                            e.printStackTrace();
                        System.out.println("message send to partition " + metadata.partition() + ", offset: " + metadata.offset());
                    }
                });

//                KeyedMessage<String,String> keyedMessage = new KeyedMessage<String, String>(topic,String.valueOf(i),"this is message : " + i);
//                kafkaProducer0820.send(keyedMessage);

                i++;
                Thread.sleep(2000);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
