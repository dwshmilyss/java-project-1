package com.yiban.dubbo.demo.provider;

import com.yiban.dubbo.demo.api.DemoService;
import com.yiban.dubbo.demo.api.DemoService1;
import org.apache.log4j.Logger;

/**
 * @auther WEI.DUAN
 * @date 2019/4/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DemoService1Impl implements DemoService1 {

    public static Logger logger = Logger.getLogger(DemoService1Impl.class);
    @Override
    public String sayHello1(String name) {
        logger.info("DemoService1Impl");
        return "DemoService1Impl " + name;
    }

}