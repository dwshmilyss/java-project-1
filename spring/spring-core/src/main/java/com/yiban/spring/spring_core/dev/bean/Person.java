package com.yiban.spring.spring_core.dev.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;

import java.util.List;
import java.util.Map;

/**
 * @auther WEI.DUAN
 * @date 2019/4/2
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Person implements BeanFactoryAware, BeanNameAware, InitializingBean, DisposableBean {
    private String name;
    private String address;
    private long phone;

    private List<String> classes;
    private Map<String,String> grades;

    private BeanFactory beanFactory;
    private String beanName;

    public Person() {
        System.out.println("调用Person的无参构造函数");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        System.out.println("【注入属性】注入属性name");
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        System.out.println("【注入属性】注入属性address");
        this.address = address;
    }

    public long getPhone() {
        return this.phone;
    }

    public void setPhone(long phone) {
        System.out.println("【注入属性】注入属性phone");
        this.phone = phone;
    }


    public List<String> getClasses() {
        return this.classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public void setGrades(Map<String, String> grades) {
        this.grades = grades;
    }

    public Map<String, String> getGrades() {
        return this.grades;
    }

    @Override
    public String toString() {
        String res =  "Person{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone=" + phone ;
        res += ",classes = ";
        for (String temp : classes) {
            res += " " + temp;
        }
        res += ",grades = ";
        for (Map.Entry<String,String> entry : grades.entrySet()) {
            res += " key = " + entry.getKey() + ",value = " + entry.getValue();
        }
        res += "}";
        return res;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("【BeanFactoryAware接口】调用BeanFactoryAware.setBeanFactory()");
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println("【BeanNameAware接口】调用BeanNameAware.setBeanName()");
        this.beanName = beanName;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("【DiposibleBean接口】调用DiposibleBean.destory()");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("【InitializingBean接口】调用InitializingBean.afterPropertiesSet()");
    }

    // 通过<bean>的init-method属性指定的初始化方法
    public void myInit() {
        System.out.println("【init-method】调用<bean>的init-method属性指定的初始化方法");
    }

    // 通过<bean>的destroy-method属性指定的初始化方法
    public void myDestory() {
        System.out.println("【destroy-method】调用<bean>的destroy-method属性指定的初始化方法");
    }
}