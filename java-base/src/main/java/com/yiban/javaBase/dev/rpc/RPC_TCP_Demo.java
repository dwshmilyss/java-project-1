package com.yiban.javaBase.dev.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于TCP协议实现RPC
 *
 * @auther WEI.DUAN
 * @date 2018/9/19
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RPC_TCP_Demo {
    public static void main(String[] args) {
        System.out.println("aa");
    }
}

interface SayHelloService {
    String sayHello(String arg);
}

class SayHelloServiceImpl implements SayHelloService {

    @Override
    public String sayHello(String arg) {
        return "hello".equalsIgnoreCase(arg) ? "hello" : "byebye";
    }
}

class Consumer {
    public static void main(String[] args) throws Exception {
        //接口名
        String interfaceName = SayHelloService.class.getName();
        //远程方法名
        Method method = SayHelloService.class.getMethod("sayHello", String.class);
        //远程方法参数列表
        Object[] params = {"hello"};

        //TCP
        Socket socket = new Socket("127.0.0.1", 1234);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        out.writeUTF(interfaceName);//接口名称
        out.writeUTF(method.getName());//方法名
        out.writeObject(method.getParameterTypes());//方法参数类型
        out.writeObject(params);//方法参数

        System.out.println("发送信息到服务端，发送的信息为：" + params[0]);

        //从远端读取返回结果
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        String result = (String) in.readObject();
        System.out.println("服务器端的返回结果为：" + result);

    }
}

class Provider{
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1234);
        Map<Object,Object> services = new HashMap<>();
        services.put(SayHelloService.class.getName(),new SayHelloServiceImpl());
        while (true){
            System.out.println("服务提供者启动，等待客户端调用.....");
            Socket socket = serverSocket.accept();
            //读取服务信息
            ObjectInputStream in  = new ObjectInputStream(socket.getInputStream());
            String interfaceName = in.readUTF();
            String methodName = in.readUTF();

            Class<?>[] parameterTypes = (Class<?>[]) in.readObject();
            Object[] arguments = (Object[]) in.readObject();
            System.out.println("客户端调用服务接口："+interfaceName +"的"+methodName+"方法");
            //执行调用
            Class serviceClass = Class.forName(interfaceName);//得到接口的class
            Object service = services.get(interfaceName);//获取服务实现类的对象
            Method method = serviceClass.getMethod(methodName,parameterTypes);

            Object result = method.invoke(service,arguments);//调用目标函数

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(result);
            System.out.println("服务端返回的结果为 = " + result);
        }

    }
}