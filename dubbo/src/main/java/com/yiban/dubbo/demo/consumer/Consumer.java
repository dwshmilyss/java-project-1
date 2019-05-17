package com.yiban.dubbo.demo.consumer;

import com.yiban.dubbo.demo.api.DemoService;
import com.yiban.dubbo.demo.api.DemoService1;
import com.yiban.dubbo.demo.api.DemoService2;
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
        DemoService1 demoService1 = (DemoService1)context.getBean("demoService1");
        DemoService2 demoService2 = (DemoService2)context.getBean("demoService2");
        // Executing remote methods
        String hello = demoService.sayHello("world");
        String hello1 = demoService1.sayHello1("world1");
        String hello2 = demoService2.sayHello2("world2");
        // Display the call result
        System.out.println(hello);
        System.out.println(hello1);
        System.out.println(hello2);
    }
}