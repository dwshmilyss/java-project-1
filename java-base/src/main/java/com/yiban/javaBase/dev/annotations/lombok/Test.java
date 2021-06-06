package com.yiban.javaBase.dev.annotations.lombok;

import lombok.extern.log4j.Log4j2;

/**
 * test for lombok
 *
 * @auther WEI.DUAN
 * @date 2017/11/30
 * @website http://blog.csdn.net/dwshmilyss
 */
@Log4j2
public class Test {
    public static void main(String[] args) {
        Person person1 = new Person();
        person1.setId("1");
        person1.setName("dw");
        person1.setIdentity("man");
        System.out.println(person1.toString());

        Person person2 = new Person();
        person2.setId("1");
        person2.setName("dw");
        person2.setIdentity("man");
        System.out.println(person2.toString());

        System.out.println(person1.equals(person2));

        log.info("lombok test");
    }
}
