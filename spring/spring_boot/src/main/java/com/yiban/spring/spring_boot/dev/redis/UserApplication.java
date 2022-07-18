package com.yiban.spring.spring_boot.dev.redis;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 启动类
 *
 * @auther WEI.DUAN
 * @date 2019/11/27
 * @website http://blog.csdn.net/dwshmilyss
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableSwagger2
@EnableFeignClients(basePackages = "com.yiban.spring.boot.dev.redis")
@EnableCaching
public class UserApplication {
}