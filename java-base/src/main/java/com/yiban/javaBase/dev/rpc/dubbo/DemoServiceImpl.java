package com.yiban.javaBase.dev.rpc.dubbo;

/**
 * impl
 *
 * @auther WEI.DUAN
 * @date 2018/7/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
