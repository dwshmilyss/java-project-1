package com.yiban.javaBase.dev.IO.net_io.NIO;

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

    // 创建读取的缓冲区(1kb)
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    //声明通道
    private ServerSocketChannel serverChannel;

    public static void main(String[] args) {
        try {
            NIOServer server = new NIOServer();
            server.initServer(8000);
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
     *
     * @param port 绑定的端口号
     * @throws IOException
     */
    public void initServer(int port) throws IOException {
        // 获得一个ServerSocketChannel通道
        serverChannel = ServerSocketChannel.open();
        // 设置通道为非阻塞
        serverChannel.configureBlocking(false);
        // 将该通道对应的ServerSocket绑定到port端口，1024代表请求挂起的最大连接数
        serverChannel.socket().bind(new InetSocketAddress(port), 1024);
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
//        int interestSet = SelectionKey.OP_ACCEPT | SelectionKey.OP_CONNECT;
//        System.out.println("interestSet = " + interestSet);
        //这里好像只能用OP_ACCEPT来注册
        SelectionKey key = serverChannel.register(selector, SelectionKey.OP_ACCEPT);
//        SelectionKey key = serverChannel.register(selector, interestSet);
        key.attach("abc");
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
            /**
             * 带参数的方法
             */
//            selector.select(1000);
            //只有当注册的事件到达时才会继续；否则该方法会一直阻塞
            selector.select();
            //获取指定selector中注册的事件的迭代器
            Iterator ite = this.selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = (SelectionKey) ite.next();
                //获取到该key后就删除，以防重复处理
                ite.remove();
                //如果是客户端请求连接事件
                if (key.isAcceptable()) {//是否可接收
                    System.out.println("attachment = " + key.attachment());//返回selecctKey的attachment，可以在注册channel的时候指定
                    key.channel();//返回该SelectionKey对应的channel
                    key.selector();//返回该SelectionKey对应的selector
                    key.interestOps();//返回该SelectionKey监控的IO操作的bit mask
                    //可以通过如下方法判断selector是否对channel的某种事件感兴趣
                    int interestSet = key.interestOps();
                    boolean isInterestedInAccept = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT;
                    boolean isInterestedInConnect = (interestSet & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT;
                    boolean isInterestedInRead = (interestSet & SelectionKey.OP_READ) == SelectionKey.OP_READ;
                    boolean isInterestedInWrite = (interestSet & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE;
                    key.readyOps();//返回一个bit mask，代表在相应的channel上可以进行的IO操作
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    //获得和客户端连接的channel
                    SocketChannel channel = server.accept();
                    //设置成非阻塞
                    channel.configureBlocking(false);
                    //这时候可以给客户端回送消息
                    channel.write(ByteBuffer.wrap(new String("sersver has received").getBytes()));
                    //在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
                    channel.register(this.selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {//是否可读
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
        //切换读写模式，读取缓冲区中的数据。此行代码一定要有
        /**
         * buffer 中的capacity、position、limit三个概念
         *      capacity：在读/写模式下都是固定的，就是我们分配的缓冲大小（容量）。
         position：类似于读/写指针，表示当前读(写)到什么位置。
         limit：在写模式下表示最多能写入多少数据，此时和capacity相同。在读模式下表示最多能读多少数据，此时和缓存中的实际数据大小相同。
         */
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
}
