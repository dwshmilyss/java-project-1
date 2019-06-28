package com.yiban.spring_kafka.dev.boot.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @auther WEI.DUAN
 * @date 2019/6/27
 * @website http://blog.csdn.net/dwshmilyss
 */
@Service
public class KafkaMessageService  {
    /** Logger **/
    private static final Logger LOGGER = Logger.getLogger(KafkaMessageService.class);

    public KafkaMessageService() {
        System.out.println("KafkaMessageService init");
    }
    static {
        System.out.println("KafkaMessageService static ");
    }

    @KafkaListener(topics = {"test_8_3"})
    public void receiveMessage(ConsumerRecord<String, String> data) {
        System.out.println("consumer message....");
        //根据不同的topic处理不同的消费逻辑
        if ("test_8_3".equals(data.topic())) {
            System.out.println(Thread.currentThread().getName() + " : topic = " + data.topic() + ",partition = " + data.partition() + ",key = " + data.key() + ",value = " + data.value());
            LOGGER.info(Thread.currentThread().getName() + " : topic = " + data.topic() + ",partition = " + data.partition() + ",key = " + data.key() + ",value = " + data.value());
        }
    }
}