package com.yiban.javaBase.dev.rpc.demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

/**
 * 服务端
 *
 * @auther WEI.DUAN
 * @date 2020/3/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Server {
    public Server() throws IOException {
        Configuration configuration = new Configuration();
        RPC.Server server = new RPC.Builder(configuration)
                .setProtocol(MyProtocol.class).setInstance(new MyProtocolImpl())
                .setBindAddress("localhost").setPort(9000).setNumHandlers(5).build();
        server.start();
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
