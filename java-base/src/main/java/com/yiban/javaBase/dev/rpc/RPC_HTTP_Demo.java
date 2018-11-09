package com.yiban.javaBase.dev.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 基于HTTP协议的RPC
 *
 * @auther WEI.DUAN
 * @date 2018/9/19
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RPC_HTTP_Demo {
    public static void main(String[] args){
        Byte b = new Byte("127");
    }
}

/**
 * 请求协议
 */
class Request {
    /**
     * 协议编码 0:GBK   1:UTF-8
     */
    private byte encode;

    //命令
    private String command;
    //命令长度
    private int commandLength;

    public byte getEncode() {
        return this.encode;
    }

    public void setEncode(byte encode) {
        this.encode = encode;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getCommandLength() {
        return this.commandLength;
    }

    public void setCommandLength(int commandLength) {
        this.commandLength = commandLength;
    }
}

/**
 * 响应协议
 */
class Response {
    /**
     * 协议编码 0:GBK   1:UTF-8
     */
    private byte encode;

    //响应
    private String response;

    public int getResponseLength() {
        return this.responseLength;
    }

    public void setResponseLength(int responseLength) {
        this.responseLength = responseLength;
    }

    public byte getEncode() {
        return this.encode;
    }

    public void setEncode(byte encode) {
        this.encode = encode;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    //响应长度
    private int responseLength;

    @Override
    public String toString() {
        return "Response{" +
                "encode=" + encode +
                ", response='" + response + '\'' +
                ", responseLength=" + responseLength +
                '}';
    }
}

/**
 * 服务端
 */
class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(1234);
        while (true) {
            Socket client = server.accept();
            //读取请求数据
            Request request = ProtocolUtil.readRequest(client.getInputStream());
            //封装响应数据
            Response response = new Response();
            response.setEncode(Encode.UTF8.getValue());
            response.setResponse(request.getCommand().equals("HELLO") ? "hello!" : "bye bye");
            response.setResponseLength(response.getResponse().length());
            //响应到客户端
            ProtocolUtil.writeResponse(client.getOutputStream(), response);
        }
    }
}

/**
 * 客户端
 */
class Client {
    public static void main(String[] args) throws Exception {
        //组装请求数据
        Request request = new Request();
        request.setCommand("HELLO");
        request.setCommandLength(request.getCommand().length());
        request.setEncode(Encode.UTF8.getValue());
        Socket client = new Socket("127.0.0.1", 1234);
        //发送请求
        ProtocolUtil.writeRequest(client.getOutputStream(), request);
        //读取相应
        Response response = ProtocolUtil.readResponse(client.getInputStream());
        System.out.println(response);
    }
}

/**
 * 工具类
 */
class ProtocolUtil {
    public static void writeRequest(OutputStream out, Request request) {
        try {
            out.write(request.getEncode());
            //write一个int值会截取其低8位传输，丢弃其高24位，因此需要将基本类型转化为字节流
            //java采用Big Endian字节序，而所有的网络协议也都是以Big Endian字节序来进行传输，所以再进行数据的传输和接收时，需要先将数据转化成Big Endian字节序
            //out.write(request.getCommandLength());
            out.write(int2ByteArray(request.getCommandLength()));
            out.write(Encode.GBK.getValue() == request.getEncode() ? request.getCommand().getBytes("GBK") : request.getCommand().getBytes("UTF8"));
            out.flush();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * 将响应输出到客户端
     *
     * @param response
     */
    public static void writeResponse(OutputStream out, Response response) {
        try {
            out.write(response.getEncode());
            out.write(int2ByteArray(response.getResponseLength()));
            out.write(Encode.GBK.getValue() == response.getEncode() ? response.getResponse().getBytes("GBK") : response.getResponse().getBytes("UTF8"));
            out.flush();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static Request readRequest(InputStream is) {
        Request request = new Request();
        try {
            //读取编码
            byte[] encodeByte = new byte[1];
            is.read(encodeByte);
            byte encode = encodeByte[0];
            //读取命令长度
            byte[] commandLengthByte = new byte[4];//缓冲区
            is.read(commandLengthByte);
            int commandLength = byte2Int(commandLengthByte);
            //读取命令
            byte[] commandByte = new byte[commandLength];
            is.read(commandByte);
            String command = Encode.GBK.getValue() == encode ? new String(commandByte, "GBK") : new String(commandByte, "UTF8");
            //组装请求返回
            request.setEncode(encode);
            request.setCommand(command);
            request.setCommandLength(commandLength);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return request;
    }

    public static Response readResponse(InputStream is) {
        Response response = new Response();
        try {
            byte[] encodeByte = new byte[1];
            is.read(encodeByte);
            byte encode = encodeByte[0];
            byte[] responseLengthByte = new byte[4];
            is.read(responseLengthByte);
            int commandLength = byte2Int(responseLengthByte);
            byte[] responseByte = new byte[commandLength];
            is.read(responseByte);
            String resContent = Encode.GBK.getValue() == encode ? new String(responseByte, "GBK") : new String(responseByte, "UTF8");
            response.setEncode(encode);
            response.setResponse(resContent);
            response.setResponseLength(commandLength);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return response;
    }

    public static int byte2Int(byte[] bytes) {
        int num = bytes[3] & 0xFF;
        num |= ((bytes[2] << 8) & 0xFF00);
        num |= ((bytes[1] << 16) & 0xFF0000);
        num |= ((bytes[0] << 24) & 0xFF000000);
        return num;
    }

    public static byte[] int2ByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

}

enum Encode {
    GBK(new Byte("0")), UTF8(new Byte("1"));
    private byte value;

    Encode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
