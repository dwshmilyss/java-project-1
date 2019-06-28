package com.yiban.spring_kafka.dev;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


/**
 * @auther WEI.DUAN
 * @date 2019/6/27
 * @website http://blog.csdn.net/dwshmilyss
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(locations = {"classpath:consumer.xml"})
public class TestConsumer {
    public static final Logger LOGGER = Logger.getLogger(TestConsumer.class);

    @Resource(name = "kafkaMessageListener")
    private  KafkaMessageListener kafkaMessageListener;

    @Resource(name = "messageListenerContainer")
    KafkaMessageListenerContainer messageListenerContainer;


    @Test
    public void TestLinstener(){
//        ContainerProperties containerProps = new ContainerProperties("test_8_3");
//        containerProps.setMessageListener(kafkaMessageListener);
//        KafkaMessageListenerContainer<Integer, String> container = createContainer(containerProps);
//        container.setBeanName("messageListenerContainer");
//        container.start();

        messageListenerContainer.start();
    }

    private static KafkaMessageListenerContainer<Integer, String> createContainer(
            ContainerProperties containerProps) {
        Map<String, Object> props = consumerProps();
        DefaultKafkaConsumerFactory<Integer, String> cf =
                new DefaultKafkaConsumerFactory<>(props);
        KafkaMessageListenerContainer<Integer, String> container =
                new KafkaMessageListenerContainer<>(cf, containerProps);
        return container;
    }

    private static Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.21.3.74:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test_8_3_g2");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
}