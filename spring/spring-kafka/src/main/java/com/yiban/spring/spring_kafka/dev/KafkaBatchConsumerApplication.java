

package com.yiban.spring.spring_kafka.dev;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Simple to Introduction
 * className: KafkaConcurrentListenerApplication
 *
 * @author EricYang
 * @version 2018/5/12 17:06
 */
@SpringBootApplication
@EnableSwagger2
public class KafkaBatchConsumerApplication {
    private static final Logger logger = LoggerFactory.getLogger(KafkaBatchConsumerApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(KafkaBatchConsumerApplication.class, args);
        logger.info("Done start Spring boot");

    }
}
