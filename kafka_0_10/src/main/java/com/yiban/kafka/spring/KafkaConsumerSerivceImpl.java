package com.yiban.kafka.spring;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/**
 * @auther WEI.DUAN
 * @date 2019/6/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerSerivceImpl implements MessageListener<String,String> {
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        //根据不同的topic处理不同的消费逻辑
        if ("test_8_3".equals(data.topic())) {
            System.out.println(Thread.currentThread().getName() + " : topic = " + data.topic() + ",partition = " + data.partition() + ",key = " + data.key() + ",value = " + data.value());
        }
    }
}