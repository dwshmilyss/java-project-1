package com.yiban.javaBase.dev.GOF.structure.decorator.demo2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {


    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Test.class);
        Logger jsonLogger = new DecoratorLogger(new JsonLogger(logger));
        jsonLogger.info("abc");
    }
}
