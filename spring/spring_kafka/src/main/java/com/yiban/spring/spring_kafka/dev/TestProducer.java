package com.yiban.spring.spring_kafka.dev;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

/**
 * @auther WEI.DUAN
 * @date 2019/6/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class TestProducer {
    public static final ClassPathXmlApplicationContext CONTEXT = new ClassPathXmlApplicationContext("producer.xml");

    public static <K, T> void sendMessage(String topic, K key, T value) {
        KafkaTemplate<K, T> kafkaTemplate = (KafkaTemplate<K, T>) CONTEXT.getBean("kafkaTemplate");
        ListenableFuture<SendResult<K, T>> listenableFuture = null;
        if (kafkaTemplate.getDefaultTopic().equals(topic)) {
            listenableFuture = kafkaTemplate.sendDefault(key, value);
        } else {
            listenableFuture = kafkaTemplate.send(topic, key, value);
        }
        //发送成功回调
        SuccessCallback<SendResult<K, T>> successCallback = new SuccessCallback<SendResult<K, T>>() {
            @Override
            public void onSuccess(SendResult<K, T> ktSendResult) {
                //成功业务逻辑
                System.out.println("发送成功 : " + ktSendResult.toString());

            }
        };
        //发送失败回调
        FailureCallback failureCallback = new FailureCallback() {
            @Override
            public void onFailure(Throwable ex) {
                //失败业务逻辑
                System.out.println("发送失败");
                throw new RuntimeException(ex);
            }
        };
        listenableFuture.addCallback(successCallback, failureCallback);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            sendMessage("test_8_3",String.valueOf(i),"this is message : " + i);
        }
    }
}