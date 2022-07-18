package com.yiban.spring.spring_kafka.dev.config;

import com.yiban.spring.spring_kafka.dev.consumer.MyListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther WEI.DUAN
 * @date 2019/7/1
 * @website http://blog.csdn.net/dwshmilyss
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Autowired
    PropsConfig propsConfig;

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String,String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(8);
        //设置为批量消费，每个批次数量在Kafka配置参数中设置ConsumerConfig.MAX_POLL_RECORDS_CONFIG
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(3000);
//        //配置手动提交 这个要配合MyListener中的Acknowledgment 一起使用
//        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
//        //异常的话直接从onMessage里抛出去，这时候spring-kafka会看你的ackOnError设置的值，是true的话会提交，false的话不提交
//        factory.getContainerProperties().setAckOnError(true);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, propsConfig.getBrokers());
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, propsConfig.getEnableAutoCommit());
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, propsConfig.getGroupId());
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, propsConfig.getAutoOffsetReset());
        //一次最多拉取50条
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);

        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return propsMap;
    }

    @Bean
    public MyListener listener() {
        return new MyListener();
    }
}