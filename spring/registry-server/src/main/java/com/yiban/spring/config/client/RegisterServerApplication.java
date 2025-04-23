package com.yiban.spring.config.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/8/11
 **/
@SpringBootApplication
@EnableEurekaServer
public class RegisterServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegisterServerApplication.class, args);
    }
}
