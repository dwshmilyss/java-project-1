package com.yiban.spring_kafka.dev;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @auther WEI.DUAN
 * @date 2019/6/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class KafkaMessageListener implements MessageListener<String,String> {
    /** Logger **/
    private static final Logger LOGGER = Logger.getLogger(KafkaMessageListener.class);

    public KafkaMessageListener() {
        System.out.println("KafkaMessageListener init");
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        System.out.println("consumer message....");
        //根据不同的topic处理不同的消费逻辑
        if ("test_8_3".equals(data.topic())) {
            System.out.println(Thread.currentThread().getName() + " : topic = " + data.topic() + ",partition = " + data.partition() + ",key = " + data.key() + ",value = " + data.value());
            LOGGER.info(Thread.currentThread().getName() + " : topic = " + data.topic() + ",partition = " + data.partition() + ",key = " + data.key() + ",value = " + data.value());
        }
    }
}