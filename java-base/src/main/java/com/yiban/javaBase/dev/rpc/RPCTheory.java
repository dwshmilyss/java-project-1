package com.yiban.javaBase.dev.rpc;

import java.io.*;

/**
 * RPC 理论
 *
 * @auther WEI.DUAN
 * @date 2018/9/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class RPCTheory {

    /**
     * 在网络上传输的数据，无论何种类型，最终都需要转化为二进制流。在面向对象的程序设计中，客户端将对象转化为二进制流发送给服务端，服务端接收数据后将二进制流转化为对象
     */
    public static byte[] serialize(Person person) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(person);
        byte[] bytes = os.toByteArray();
        os.close();
        out.close();
        return bytes;
    }

    /**
     * 反序列化
     * @return
     */
    public static Person deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(is);
        Person person = (Person) in.readObject();
        is.close();
        in.close();
        return person;
    }

    public static void main(String[] args){
        Person person = new Person("dw",35);

        try {
            Person newPerson = deserialize(serialize(person));
            System.out.println(newPerson);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
