package com.yiban.javaBase.dev.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.io.UnsafeInput;
import org.apache.spark.util.SizeEstimator;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @auther WEI.DUAN
 * @date 2018/12/25
 * @website http://blog.csdn.net/dwshmilyss
 * 对比JAVA原生的序列化类和Kyro的性能
 */
public class KyroAndJavaSerDemo {
    static final StopWatch stopWatch = new StopWatch();

    public static void main(String[] args) throws Exception {
//        javaSerializeTest();
//        kyroSerializeTest();
//        kyroSerializeTest1();
//        kyroSerializeByDirect();
        String path = KyroAndJavaSerDemo.class.getClassLoader().getResource("transform.data").getPath();
        System.out.println("path = " + path);
        derializeTest(path);

    }

    public static void javaSerializeTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        long begin = System.currentTimeMillis();
        People people = new People("zhangsan", "man", 23);
        oos.writeObject(people);
        long end = System.currentTimeMillis();
        System.out.println(baos.toByteArray().length);
        System.out.println("java encode time is " + (end - begin));


        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        stopWatch.start();
        People people1 = (People) ois.readObject();
        stopWatch.stop();
        System.out.println(people1.name + "," + people1.sex);
        System.out.println("java decode time is " + stopWatch.getTotalTimeMillis());
    }


    public static void kyroSerializeTest() throws Exception {
        Kryo kryo = new Kryo();
        //注册类
        Registration registration = kryo.register(People.class);

        Output output = new Output(0, 4096);
        stopWatch.start();
        People people = new People("zhangsan", "man", 23);
        kryo.writeObject(output, people);
        byte[] bb = output.toBytes();
        stopWatch.stop();
        System.out.println(bb.length);
        System.out.println("kryo encode time is " + (stopWatch.getTotalTimeMillis()));
        output.flush();

        Input input = new Input(bb);
        stopWatch.start();
        People s = (People) kryo.readObject(input, registration.getType());
        stopWatch.stop();
        System.out.println(s.name + "," + s.sex);
        System.out.println("kryo decode time is " + (stopWatch.getTotalTimeMillis()));
    }

    public static void derializeTest(String path) throws Exception {
        Kryo kryo = new Kryo();
        kryo.register(scala.collection.immutable.$colon$colon.class, 60);
        Registration registration = kryo.register(Transform_Data.class);
        Input input = new Input(new FileInputStream(path));
        Transform_Data s = (Transform_Data) kryo.readObject(input, registration.getType());
        System.out.println("s = " + s);
    }

    public static void kyroSerializeByDirect() throws Exception {
        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        //注册类
        Registration registration = kryo.register(People.class);
        UnsafeOutput output = new UnsafeOutput(0,4096);
        stopWatch.start();
        People people = new People("zhangsan", "man", 23);
        kryo.writeObject(output, people);
        byte[] bb = output.toBytes();
        stopWatch.stop();
        System.out.println(bb.length);
        System.out.println("kryo encode time is " + (stopWatch.getTotalTimeMillis()));
        output.flush();

        UnsafeInput input = new UnsafeInput(bb);
        stopWatch.start();
        People s = (People) kryo.readObject(input, registration.getType());
        stopWatch.stop();
        System.out.println(s.name + "," + s.sex);
        System.out.println("kryo decode time is " + (stopWatch.getTotalTimeMillis()));
    }
}

class People implements Serializable{

    private static final long serialVersionUID = -6090026784744601182L;
    public String name;
    public String sex;
    public int age;

    /**
     * Kryo的使用首先要将类进行绑定，其次该类还需要提供默认构造函数。
     */
    public People() {
    }

    public People(String name, String sex, int age) {
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}
