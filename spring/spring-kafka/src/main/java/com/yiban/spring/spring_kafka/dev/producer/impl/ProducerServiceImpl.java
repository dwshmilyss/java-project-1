package com.yiban.spring.spring_kafka.dev.producer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.spring.spring_kafka.dev.producer.ProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @auther WEI.DUAN
 * @date 2019/7/1
 * @website http://blog.csdn.net/dwshmilyss
 */
@Service
public class ProducerServiceImpl implements ProducerService {
    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerServiceImpl.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void send(String topic, String str, int count) {
        for (int i = 0; i < count; i++) {
            kafkaTemplate.send(topic, str);
        }
    }

    @Override
    public void sendJson(String topic, String json, int count) {
        for (int i = 0; i < count; i++) {
            sendJson(topic,json);
        }
    }

    private void sendJson(String topic, String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("topic", topic);
        jsonObject.put("ts", System.currentTimeMillis() + "");
        LOGGER.info("json+++++++++++++++++++++  message = {}", jsonObject.toJSONString());

        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, jsonObject.toJSONString());
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.info("msg OK. " + result.toString());
            }

            @Override
            public void onFailure(Throwable ex) {
                LOGGER.error("msg send failed.", ex);
            }
        });
    }
}