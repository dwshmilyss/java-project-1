package com.yiban.javaBase.dev.rpc;

/**
 * test
 *
 * @auther WEI.DUAN
 * @date 2018/7/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    public static void main(String[] args) {
        HelloWorldServiceImpl helloWorldServiceImpl = new HelloWorldServiceImpl();
        RPCProxyClient rpcProxyClient = new RPCProxyClient(helloWorldServiceImpl);
        HelloWorldService proxy = (HelloWorldService) rpcProxyClient.getProxy();
        proxy.sayHello("test");
    }
}
