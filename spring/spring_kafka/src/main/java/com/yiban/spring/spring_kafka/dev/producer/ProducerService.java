package com.yiban.spring.spring_kafka.dev.producer;

public interface ProducerService {
    void send(String topic, String str, int count);
    void sendJson(String topic, String json, int count);
}
