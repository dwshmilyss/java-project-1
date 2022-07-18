package com.yiban.javaBase.dev.rpc.serialize;

import com.alibaba.com.caucho.hessian.io.HessianInput;
import com.alibaba.com.caucho.hessian.io.HessianOutput;

import java.io.*;

/**
 * RPC 理论
 *
 * @auther WEI.DUAN
 * @date 2018/9/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SerializeUtil {

    /**
     * 在网络上传输的数据，无论何种类型，最终都需要转化为二进制流。在面向对象的程序设计中，客户端将对象转化为二进制流发送给服务端，服务端接收数据后将二进制流转化为对象
     */

    /**
     * java的序列化和反序列化
     *
     * @param person
     * @return
     * @throws IOException
     */
    public static byte[] serializeByJava(Person person) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(person);
        byte[] bytes = os.toByteArray();
        os.close();
        out.close();
        return bytes;
    }

    public static Person deserializeByJava(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        ObjectInputStream in = new ObjectInputStream(is);
        Person person = (Person) in.readObject();
        is.close();
        in.close();
        return person;
    }


    /**
     * 阿里的 Hessian 的序列化和反序列化
     */
    public static byte[] serializeByHessian(Person person) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput out = new HessianOutput(os);
        out.writeObject(person);
        byte[] bytes = os.toByteArray();
        os.close();
        out.close();
        return bytes;
    }

    public static Person deserializeByHessian(byte[] bytes) throws IOException {
        ByteArrayInputStream isH = new ByteArrayInputStream(bytes);
        HessianInput inH = new HessianInput(isH);
        Person newPerson = (Person) inH.readObject();
        isH.close();
        inH.close();
        return newPerson;
    }


    public static void main(String[] args) {
        Person person = new Person("dw", 35);

        try {
            Person newPerson = deserializeByJava(serializeByJava(person));
            Person newPerson1 = deserializeByHessian(serializeByHessian(person));
            System.out.println(newPerson);
            System.out.println(newPerson1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
