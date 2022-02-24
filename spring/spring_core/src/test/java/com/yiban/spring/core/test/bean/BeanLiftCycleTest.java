package com.yiban.spring.core.test.bean;


import com.yiban.spring.spring_core.dev.bean.Person;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @auther WEI.DUAN
 * @date 2019/4/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class BeanLiftCycleTest {
    @Test
    public void test(){
        System.out.println("初始化容器");
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        System.out.println("容器初始化成功");
        Person person = applicationContext.getBean("person", Person.class);
        System.out.println(person);
        System.out.println("开始关闭容器");
        ((ClassPathXmlApplicationContext) applicationContext).registerShutdownHook();
    }


    @Test
    public void test1(){
        Long.valueOf("15900000000");
    }
}