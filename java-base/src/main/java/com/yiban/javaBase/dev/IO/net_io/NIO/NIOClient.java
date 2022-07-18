package com.yiban.javaBase.dev.IO.net_io.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;

/**
 * NIOClient
 *
 * @auther WEI.DUAN
 * @date 2017/9/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class NIOClient extends Thread {
    //管道管理器
    private Selector selector;

    public static void main(String[] args) throws IOException {
        new NIOClient().init("127.0.0.1", 8000).listen();
    }

    public NIOClient init(String serverIp, int port) throws IOException {
        //获取socket通道
        SocketChannel channel = SocketChannel.open();

        channel.configureBlocking(false);
        //获得通道管理器
        selector = Selector.open();

        //客户端连接服务器，需要调用channel.finishConnect();才能实际完成连接。
        channel.connect(new InetSocketAddress(serverIp, port));
        //为该通道注册SelectionKey.OP_CONNECT事件
        channel.register(selector, SelectionKey.OP_CONNECT);
        return this;
    }

    public void listen() throws IOException {
        System.out.println("客户端启动");
        //轮询访问selector
        while (true) {
            //选择注册过的io操作的事件(第一次为SelectionKey.OP_CONNECT)
            selector.select();
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                //删除已选的key，防止重复处理
                ite.remove();
                if (key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();

                    //如果正在连接，则完成连接
                    if (channel.isConnectionPending()) {
                        channel.finishConnect();
                    }

                    channel.configureBlocking(false);
                    //向服务器发送消息
                    channel.write(ByteBuffer.wrap(new String("p1").getBytes("UTF-8")));

                    //连接成功后，注册接收服务器消息的事件
                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                } else if (key.isReadable()) { //有可读数据事件。
                    SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer);
//                    byte[] data = buffer.array();
                    //用这样的方式读取就不用调用buffer.flip()
                    byte[] data = Arrays.copyOfRange(buffer.array(), 0, buffer.position());
                    String message = new String(data, "UTF-8");
                    System.out.println("recevie message from server:, size:" + buffer.position() + " msg: " + message);
                    //写消息到服务端(另外还有一种方法可以写数据)
                    ByteBuffer outbuffer = ByteBuffer.wrap(("client.".concat(message)).getBytes());
                    channel.write(outbuffer);
                    //
                    doWrite(channel,message);
                }
            }
        }
    }

    /**
     * 另外一种写消息的方式
     *
     * @param socketChannel
     * @param msg
     * @throws IOException
     */
    private void doWrite(SocketChannel socketChannel, String msg) throws IOException {
        //将消息转换为字节数组
        byte[] bytes = msg.getBytes("UTF-8");
        //根据数组的长度创建缓冲区
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        //将字节数组复制到缓冲区
        writeBuffer.put(bytes);
        //转换读写模式
        writeBuffer.flip();
        //发送缓冲区中的数据
        socketChannel.write(writeBuffer);
    }
}
