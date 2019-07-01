package com.yiban.spring_kafka.dev.boot.producer;

public interface ProducerService {
    void send(String topic, String str, int count);
    void sendJson(String topic, String json, int count);
}
