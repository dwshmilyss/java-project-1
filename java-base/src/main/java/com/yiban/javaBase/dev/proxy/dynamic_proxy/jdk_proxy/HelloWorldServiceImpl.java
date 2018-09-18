package com.yiban.javaBase.dev.proxy.dynamic_proxy.jdk_proxy;

/**
 * 服务端实现类
 *
 * @auther WEI.DUAN
 * @date 2018/7/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class HelloWorldServiceImpl implements HelloWorldService {
    @Override
    public String sayHello(String msg) {
        String result = "hello world " + msg;
        System.out.println(result);
        return result;
    }
}
