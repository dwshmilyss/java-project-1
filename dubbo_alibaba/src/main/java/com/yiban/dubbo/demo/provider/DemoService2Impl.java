package com.yiban.dubbo.demo.provider;

import com.yiban.dubbo.demo.api.DemoService;
import com.yiban.dubbo.demo.api.DemoService2;
import org.apache.log4j.Logger;

/**
 * @auther WEI.DUAN
 * @date 2019/4/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DemoService2Impl implements DemoService2 {

    public static Logger logger = Logger.getLogger(DemoService2Impl.class);
    @Override
    public String sayHello2(String name) {
        logger.info("DemoService2Impl");
        return "DemoService2Impl " + name;
    }

}