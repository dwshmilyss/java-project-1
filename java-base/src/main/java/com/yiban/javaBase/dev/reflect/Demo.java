package com.yiban.javaBase.dev.reflect;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import sun.reflect.ReflectionFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Demo implements Serializable {

    private static final long serialVersionUID = -4142821318723942007L;

    public static void main(String[] args) throws Exception {
        Demo.class.getDeclaredConstructor(null).newInstance(null);
        try {
            for (int i = 1; i <= 2; i++) {
//                getClass("com.yiban.javaBase.dev.reflect.Demo").getConstructor(null).newInstance(null);
//                Class.forName("com.yiban.javaBase.dev.reflect.Demo").getConstructor(null).newInstance(null);
                Class.forName("com.yiban.javaBase.dev.reflect.Demo").getConstructor(String.class, Boolean.TYPE).newInstance("aa", true);
//                Class.forName("com.yiban.javaBase.dev.reflect.Demo").getDeclaredConstructor(null).newInstance(null);
                ReflectionFactory.getReflectionFactory().newConstructorForSerialization(Demo.class).newInstance(null);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        Kryo kryo = new Kryo();
//        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
//        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        kryo.setInstantiatorStrategy(new Kryo.DefaultInstantiatorStrategy());
        kryo.register(SomeClass.class);
        SomeClass object = new SomeClass("aa");
        object.value = "Hello Kryo!";

        Output output = new Output(new FileOutputStream("file.bin"));
        kryo.writeObject(output, object);
        output.close();

        Input input = new Input(new FileInputStream("file.bin"));
        SomeClass object2 = kryo.readObject(input, SomeClass.class);
        input.close();
    }

    static public class SomeClass {

        String value;


        public SomeClass(String value) {
            this.value = value;
        }

        public SomeClass() {
        }
    }

    private static int x;

    public Demo() {
//        new Throwable("" + x++).printStackTrace();
    }

    public Demo(String str, boolean aa) {
//        new Throwable("" + x++).printStackTrace();
    }

    private static Map<String, Class<?>> clazzCache = new HashMap<>();

    private static Class<?> getClass(String clazzName) {
        if (!clazzCache.containsKey(clazzName)) {
            try {
                Class<?> clazz = Class.forName(clazzName);
                clazzCache.put(clazzName, clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Unable to load class", e);
            }
        }
        return clazzCache.get(clazzName);
    }
}
