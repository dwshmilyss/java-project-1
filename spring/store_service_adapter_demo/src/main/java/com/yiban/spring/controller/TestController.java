package com.yiban.spring.controller;

import com.yiban.spring.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author david.duan
 * @packageName com.yiban.spring.controller
 * @className TestController
 * @date 2025/4/21
 * @description
 */
@RestController
public class TestController {
    private static final Logger logger = LogManager.getLogger(TestController.class);

    @Autowired
    FileService fileService;

    @GetMapping("/hello")
    public String hello() {
        logger.info("Hello log4j2!");
        logger.error("Something went wrong...");
        //这里可以看到控制台会根据nacos中不同的配置输出不同的结果(minio or aliyun)
        fileService.createBucket("test");
        return "hello";
    }
}
