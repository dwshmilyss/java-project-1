package com.yiban.javaBase.dev.proxy.dynamic_proxy;

import com.yiban.javaBase.dev.proxy.dynamic_proxy.cglib_proxy.BookFacadeImpl;
import com.yiban.javaBase.dev.proxy.dynamic_proxy.cglib_proxy.BookFacadeProxy;
import com.yiban.javaBase.dev.proxy.dynamic_proxy.jdk_proxy.HelloWorldService;
import com.yiban.javaBase.dev.proxy.dynamic_proxy.jdk_proxy.HelloWorldServiceImpl;
import com.yiban.javaBase.dev.proxy.dynamic_proxy.jdk_proxy.RPCProxyClient;

/**
 * test
 *
 * @auther WEI.DUAN
 * @date 2018/7/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        /**
         * jdk dynamic proxy
         */
        HelloWorldServiceImpl helloWorldServiceImpl = new HelloWorldServiceImpl();
        RPCProxyClient rpcProxyClient = new RPCProxyClient(helloWorldServiceImpl);
        HelloWorldService proxy = (HelloWorldService) rpcProxyClient.getProxy();
        proxy.sayHello("test");


        /**
         * cglib dynamic proxy
         */
        BookFacadeImpl bookFacade=new BookFacadeImpl();
        BookFacadeProxy cglib=new BookFacadeProxy();
        BookFacadeImpl bookCglib=(BookFacadeImpl)cglib.getInstance(bookFacade);
        bookCglib.addBook();
    }
}
