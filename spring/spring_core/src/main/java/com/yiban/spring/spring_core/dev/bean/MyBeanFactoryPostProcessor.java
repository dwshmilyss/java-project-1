package com.yiban.spring.spring_core.dev.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @auther WEI.DUAN
 * @date 2019/4/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    public MyBeanFactoryPostProcessor() {
        System.out.println("这是BeanFactoryPostProcessor实现类构造器！！");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        System.out.println("BeanFactoryPostProcessor调用postProcessBeanFactory方法");
        BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition("person");
//        beanDefinition.getPropertyValues().addPropertyValue("phone","110");
//        beanDefinition.getPropertyValues().addPropertyValue("name","dw");
//        beanDefinition.getPropertyValues().addPropertyValue("address","henan");
    }
}