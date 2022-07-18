package com.yiban.javaBase.dev.redis;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * 自己实现的redis客户端
 *
 * @auther WEI.DUAN
 * @date 2019/4/12
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ManulRedisClient<K, V> {
    OutputStream outputStream;
    InputStream inputStream;

    public ManulRedisClient(String host, int port) throws Exception {
        //建立连接
        Socket socket = new Socket();
        // ->@wjw_add
        socket.setReuseAddress(true);
        socket.setKeepAlive(true); // Will monitor the TCP connection is
        // valid
        socket.setTcpNoDelay(true); // Socket buffer Whetherclosed, to
        // ensure timely delivery of data
        socket.setSoLinger(true, 0); // Control calls close () method,
        socket.connect(new InetSocketAddress(host,port),5000);
        socket.setSoTimeout(2000);
        //往外写数据
        outputStream = socket.getOutputStream();
        //读取服务器端数据，读取从服务器端过来的流
        inputStream = socket.getInputStream();
    }

    /**
     * 自己实现set方法
     *
     * @param key
     * @param value
     * @throws Exception
     */
    public void set(String key, String value) throws Exception {
        //1.组装redis命令
        /**
         *   *3
         *   $3
         *   SET
         *   $7  这个7是key的字节长度
         *   k123456
         *   $21  这个21是value的字节长度     其他的都是固定的指令
         *   hello I am is wangwei
         */
        StringBuffer command = new StringBuffer();
        command.append("*3").append("\r\n");
        command.append("$3").append("\r\n");
        command.append("SET").append("\r\n");

        command.append("$").append(key.getBytes().length).append("\r\n");
        command.append(key).append("\r\n");

        command.append("$").append(value.getBytes().length).append("\r\n");
        command.append(value).append("\r\n");

        //2.把命令发给redis服务器
        outputStream.write(command.toString().getBytes());
        System.out.println("发送成功");

        //3.接收服务器端的响应
        byte[] response = new byte[1024];
        inputStream.read(response);
        System.out.println("接收到响应：" + new String(response,"UTF-8"));

    }


    /**
     * 这个是实现redis客户端的get方法
     */
    public void get(String key) throws Exception {
        //1.需要先组装redis的指令
        StringBuffer data = new StringBuffer();
        data.append("*3").append("\r\n");
        data.append("$3").append("\r\n");
        data.append("GET").append("\r\n");

        data.append("$").append(key.getBytes().length).append("\r\n");
        data.append(key).append("\r\n");


        //2.然后把指令发给Redis的服务器端
        outputStream.write(data.toString().getBytes());
        System.out.println("get方法发送成功----->" + data.toString());

        //3.把接受服务器端进行响应
        byte[] response = new byte[1024];
        inputStream.read(response);
        System.out.println("get方法接收到响应---->" + new String(response,"UTF-8"));
        outputStream.close();
        inputStream.close();
    }

    public static void main(String[] args) throws Exception {
        ManulRedisClient manulRedisClient = new ManulRedisClient("10.21.3.77", 6379);
//        manulRedisClient.set("k123456", "hello i am is dw");

        manulRedisClient.get("k123456");
    }
}