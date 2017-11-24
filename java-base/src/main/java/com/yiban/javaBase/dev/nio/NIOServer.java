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

    // 创建读取的缓冲区(1024kb)
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    //声明通道
    private ServerSocketChannel serverChannel;

    /**
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
     *
     * @param port 绑定的端口号
     * @throws IOException
     */
    public void initServer(int port) throws IOException {
        // 获得一个ServerSocket通道
        serverChannel = ServerSocketChannel.open();
        // 设置通道为非阻塞
        serverChannel.configureBlocking(false);
        // 将该通道对应的ServerSocket绑定到port端口
        serverChannel.socket().bind(new InetSocketAddress(port));
        /**
         * 获得一个通道管理器
         * 当调用Selector.open()时，选择器通过专门的工厂SelectorProvider来创建Selector的实现，SelectorProvider屏蔽了不同操作系统及版本创建实现的差异性
         */
        this.selector = Selector.open();
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
        //当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
        /**
         * 选择器为通道服务，通道事先告诉选择器：“我对某些事件感兴趣，如可读、可写等“，
         * 选择器在接受了一个或多个通道的委托后，开始选择工作，它的选择工作就完全交给操作系统，
         * linux下即为poll或epoll
         */
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
//        buffer.clear();
        //将管道中的字节流读取到缓冲区中
        int cn = channel.read(buffer);
        buffer.flip();
        //如果读到的客户端数据不为空
        if (cn > 0) {
            byte[] data = buffer.array();
            String msg = new String(data).trim();
            System.out.println("服务端收到信息：" + msg);
            System.out.println("[系统消息提示]服务器发现[" + msg + "]玩家上线");
            String returnMsg = "[系统消息提示][" + msg + "]玩家上线成功";
            ByteBuffer outBuffer = ByteBuffer.wrap(returnMsg.getBytes("UTF-8"));
            while (outBuffer.hasRemaining()) {
                channel.write(outBuffer);// 将消息回送给客户端
            }
        } else {
            System.out.println("[系统消息提示]玩家下线");
            //检测到客户端关闭（玩家下线），删除该selectionKey监听事件，否则会一直收到这个selectionKey的动作
            key.cancel();
        }
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
