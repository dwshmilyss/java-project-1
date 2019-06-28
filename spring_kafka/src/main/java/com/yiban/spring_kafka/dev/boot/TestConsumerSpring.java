package com.yiban.spring_kafka.dev.boot;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @auther WEI.DUAN
 * @date 2019/6/27
 * @website http://blog.csdn.net/dwshmilyss
 */
@SpringBootApplication
public class TestConsumerSpring {
    public static final Logger LOGGER = Logger.getLogger(TestConsumerSpring.class);

    public static void main(String[] args) {
        SpringApplication.run(TestConsumerSpring.class, args);
    }
}