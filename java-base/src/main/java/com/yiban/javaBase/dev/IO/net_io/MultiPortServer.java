package com.yiban.javaBase.dev.IO.net_io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * MultiPortServer
 * 可以绑定多个端口的nio socket server
 *
 * @auther WEI.DUAN
 * @date 2017/9/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MultiPortServer {
    private int[] ports;

    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    public MultiPortServer(int[] ports) throws IOException {
        this.ports = ports;
        listen();
    }

    public static void main(String[] args2) {
        String args[] = {"9001", "9002", "9003"};
        if (args.length <= 0) {
            System.err.println("Usage: java MultiPortEcho port [port port ...]");
            System.exit(1);
        }
        int ports[] = new int[args.length];
        for (int i = 0; i < args.length; ++i) {
            ports[i] = Integer.parseInt(args[i]);
        }
        try {
            new MultiPortServer(ports);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listen() throws IOException {
        Selector selector = Selector.open();
        for (int i = 0; i < ports.length; i++) {
            ServerSocketChannel channel = ServerSocketChannel.open();
            channel.configureBlocking(false);
            ServerSocket ss = channel.socket();
            InetSocketAddress address = new InetSocketAddress(ports[i]);
            ss.bind(address);//监听一个端口

            //注册到selector
            //register的第一个参数永远都是selector
            //第二个参数是我们要监听的事件
            //OP_ACCEPT是新建立连接的事件
            //也是适用于ServerSocketChannel的唯一事件类型
            SelectionKey key = channel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("going to listen on " + ports[i]);
        }

        while (true) {
            //这个方法会阻塞，直到至少有一个已注册的事件发生。当一个或者更多的事件发生时
            //select()方法会返回所发生的事件的数量
            int num = selector.select();
            //返回发生了事件的 SelectionKey 对象的一个 集合
            Set selectedKeys = selector.selectedKeys();
            //我们通过迭代 SelectionKeys 并依次处理每个 SelectionKey 来处理事件
            //对于每一个 SelectionKey，您必须确定发生的是什么 I/O 事件，以及这个事件影响哪些 I/O 对象。
            Iterator it = selectedKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                //监听新连接。我们仅注册了 ServerSocketChannel
                //并且仅注册它们“接收”事件。为确认这一点
                //我们对 SelectionKey 调用 readyOps() 方法，并检查发生了什么类型的事件
                //下面这个if判断条件可以用key.isAcceptable()代替（其实就是key.isAcceptable()中的代码）
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    //接收了一个新连接。因为我们知道这个服务器套接字上有一个传入连接在等待
                    //所以可以安全地接受它；也就是说，不用担心 accept() 操作会阻塞
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    // Read the data
                    SocketChannel sc = (SocketChannel) key.channel();
                    // Echo data
                    int bytesEchoed = 0;
                    while (true) {
                        buffer.clear();
                        int r = sc.read(buffer);
                        if (r <= 0) {
                            break;
                        }
                        buffer.flip();
                        sc.write(buffer);
                        bytesEchoed += r;
                    }
                    System.out.println("Echoed " + bytesEchoed + " from " + sc);
                    it.remove();
                }

            }
        }
    }
}
