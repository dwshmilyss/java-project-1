package com.yiban.netty4.dev.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import tools.LittleEndian;

import java.util.List;

/**
 * MyNettyDecoder
 * Python中struct模块支持大小字节序的pack和unpack，在Java中可以用下面的两个方法实现小字节序字节数组转int和int转小字节序字节数组
 * 大字节序表示一个数的话，用高字节位的存放数字的低位
 * 而小字节序和大字节序正好相反，用高字节位存放数字的高位。
 * @auther WEI.DUAN
 * @date 2017/9/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyNettyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 如果没有接收完Header部分（4字节），直接退出该方法
        if(in.readableBytes() >= 4) {

            // 标记开始位置，如果一条消息没传输完成则返回到这个位置
            in.markReaderIndex();

            byte[] bytes = new byte[4];
            in.readBytes(bytes); // 读取4字节的Header

            int bodyLength = LittleEndian.getLittleEndianInt(bytes); // header按小字节序转int

            // 如果body没有接收完整
            if(in.readableBytes() < bodyLength) {
                in.resetReaderIndex(); // ByteBuf回到标记位置
            } else {
                byte[] bodyBytes = new byte[bodyLength];
                in.readBytes(bodyBytes);
                String body = new String(bodyBytes, "UTF-8");
                out.add(body); // 解析出一条消息
            }
        }
    }
}
