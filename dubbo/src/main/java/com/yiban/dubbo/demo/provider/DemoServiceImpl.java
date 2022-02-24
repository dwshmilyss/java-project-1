package com.yiban.dubbo.demo.provider;

import com.yiban.dubbo.demo.api.DemoService;
import org.apache.log4j.Logger;

/**
 * @auther WEI.DUAN
 * @date 2019/4/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DemoServiceImpl implements DemoService {

    public static Logger logger = Logger.getLogger(DemoServiceImpl.class);
    @Override
    public String sayHello(String name) {
        logger.info("aaaa");
        return "hello " + name;
    }

}