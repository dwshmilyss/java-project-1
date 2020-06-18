package com.yiban.javaBase.dev.rpc.demo;

import org.apache.hadoop.ipc.ProtocolSignature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 自定义RPC协议的实现类
 *
 * @auther WEI.DUAN
 * @date 2020/3/27
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyProtocolImpl implements MyProtocol {
    @Override
    public String echo() throws IOException {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("time is " + simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }

    @Override
    public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
        return MyProtocol.versionID;
    }

    @Override
    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return new ProtocolSignature(MyProtocol.versionID, null);
    }
}
