package com.yiban.javaBase.dev.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIOServer
 *
 * @auther WEI.DUAN
 * @date 2017/9/1
 * @website http://blog.csdn.net/dwshmilyss
 */
public class NIOServer {
    //通道管理器
    private Selector selector;

    /**
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
     *
     * @param port 绑定的端口号
     * @throws IOException
     */
    public void initServer(int port) throws IOException {
        // 获得一个ServerSocket通道
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 设置通道为非阻塞
        serverChannel.configureBlocking(false);
        // 将该通道对应的ServerSocket绑定到port端口
        serverChannel.socket().bind(new InetSocketAddress(port));
        // 获得一个通道管理器
        this.selector = Selector.open();
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
        //当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
     *
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public void listen() throws IOException {
        System.out.println("服务端启动成功！");
        //轮询访问selector
        for (; ; ) {
            //当注册的事件到达时，方法返回；否则该方法会一直阻塞
            selector.select();
            //获取selector中注册的事件的迭代器
            Iterator ite = this.selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = (SelectionKey) ite.next();
                //获取到该key后就删除，以防重复处理
                ite.remove();
                //如果是客户端请求连接事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //获得和客户端连接的channel
                    SocketChannel channel = server.accept();
                    //设置成非阻塞
                    channel.configureBlocking(false);
                    //这时候可以给客户端回送消息
                    channel.write(ByteBuffer.wrap(new String("sersver has received").getBytes()));
                    //在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
                    channel.register(this.selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {//如果获取了可读的event
                    read(key);
                }
            }
        }
    }

    /**
     * 处理读取客户端发来的信息 的事件
     *
     * @param key
     * @throws IOException
     */
    private void read(SelectionKey key) throws IOException {
        // 服务器可读取消息:得到事件发生的Socket通道
        SocketChannel channel = (SocketChannel) key.channel();
        // 创建读取的缓冲区(10byte)
        ByteBuffer buffer = ByteBuffer.allocate(10);
        //将管道中的字节流读取到缓冲区中
        channel.read(buffer);
        byte[] data = buffer.array();
        String msg = new String(data).trim();
        System.out.println("服务端收到信息：" + msg);
        ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
        channel.write(outBuffer);// 将消息回送给客户端
    }

    public static void main(String[] args) {
        try {
            NIOServer server = new NIOServer();
            server.initServer(8000);
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
