package com.yiban.netty4.dev.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;

/**
 * @auther WEI.DUAN
 * @date 2017/9/1
 * @website http://blog.csdn.net/dwshmilyss
 */
public class HelloServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 收到消息直接打印输出
        System.out.println(ctx.channel().remoteAddress() + " Say : " + msg);
        /**
         * 返回客户端消息 - 我已经接收到了你的消息
         * 在channelHandlerContent自带一个writeAndFlush方法。方法的作用是写入Buffer并刷入。
         *   注意:在3.x版本中此处有很大区别。在3.x版本中write()方法是自动flush的。在4.x版本的前面几个版本也是一样的。
         *   但是在4.0.9之后修改为WriteAndFlush。普通的write方法将不会发送消息。需要手动在write之后flush()一次
         */
        ctx.writeAndFlush("Received your message !\n");
    }

    /**
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * 这里channeActive的意思是当连接活跃(建立)的时候触发.输出消息源的远程地址。并返回欢迎消息。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service!\n");
        super.channelActive(ctx);
    }
}
