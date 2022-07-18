package com.yiban.mina.dev;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import tools.LittleEndian;

/**
 * MyMinaEncoder
 *
 * @auther WEI.DUAN
 * @date 2017/9/4
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyMinaEncoder extends ProtocolEncoderAdapter {
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        String msg = (String) message;
        byte[] bytes = msg.getBytes("UTF-8");
        int length = bytes.length;
        byte[] header = LittleEndian.toLittleEndian(length);//按小字节序转成字节数组

        IoBuffer buffer = IoBuffer.allocate(length+4);
        buffer.put(header);//header
        buffer.put(bytes);//body
        buffer.flip();
        out.write(buffer);
    }
}
