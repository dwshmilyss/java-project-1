package com.yiban.dubbo.demo.consumer;

import com.yiban.dubbo.demo.api.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @auther WEI.DUAN
 * @date 2019/4/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"consumer.xml"});
        context.start();
        // Obtaining a remote service proxy
        DemoService demoService = (DemoService)context.getBean("demoService");
        // Executing remote methods
        String hello = demoService.sayHello("world");
        // Display the call result
        System.out.println(hello);
    }
}