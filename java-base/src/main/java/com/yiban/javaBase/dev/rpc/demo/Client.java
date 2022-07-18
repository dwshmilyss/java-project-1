package com.yiban.javaBase.dev.rpc.demo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;
import	java.net.InetSocketAddress;

/**
 * 客户端
 *
 * @auther WEI.DUAN
 * @date 2020/3/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Client {

    public Client() throws IOException {
        InetSocketAddress address = new InetSocketAddress("localhost", 9000);
        MyProtocol proxy = RPC.getProxy(MyProtocol.class, MyProtocol.versionID, address, new Configuration());
        proxy.echo();
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }
}
