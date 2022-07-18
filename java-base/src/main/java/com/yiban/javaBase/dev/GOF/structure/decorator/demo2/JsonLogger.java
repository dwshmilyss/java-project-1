package com.yiban.javaBase.dev.GOF.structure.decorator.demo2;

import org.slf4j.Logger;

/**
 * 具体的装饰器类
 * @auther WEI.DUAN
 * @date 2018/7/24
 * @website http://blog.csdn.net/dwshmilyss
 */
public class JsonLogger extends DecoratorLogger{
    public JsonLogger(Logger logger) {
        super(logger);
    }

    @Override
    public void info(String msg) {
        System.out.println("================");
        this.logger.info(msg);
        System.out.println("================");
    }

    @Override
    public void error(String msg) {
        System.out.println("================");
        this.logger.error(msg);
        System.out.println("================");
    }
}
