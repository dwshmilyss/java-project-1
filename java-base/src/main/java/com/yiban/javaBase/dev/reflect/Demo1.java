package com.yiban.javaBase.dev.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * demo1
 *
 * @auther WEI.DUAN
 * @create 2017/9/11
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class Demo1 {
    public static void main(String[] args) throws Exception {
//        List<String> params = getParameterNameByJava8(Demo1.class,"test");
//        for (String s:
//             params) {
//            System.out.println(s);
//        }

//        Field[] fields = Test.class.getFields();
//        for (Field field :
//                fields) {
//            System.out.println(field.getName());
//        }

        getFields(new Test());

        PrivateClass2 p = new PrivateClass2();
        Class<?> classType = p.getClass();
//
//        Field field = classType.getDeclaredField("name");
//
//        field.setAccessible(true); // 抑制Java对修饰符的检查
////        field.set(p, "lisi");
//
//        System.out.println(field.getName() +" " +field.get(p));


        // 获取Method对象
        Method method = classType.getDeclaredMethod("sayHello",
                new Class[]{String.class,Integer.class});

        method.setAccessible(true); // 抑制Java的访问控制检查
        // 如果不加上上面这句，将会Error: TestPrivate can not access a member of class PrivateClass with modifiers "private"
        String str = (String) method.invoke(p, new Object[]{"zhangsan",18});

        System.out.println(str);
    }

    /**
     * java8 可以获取参数的名称
     * idea中找到File->Settings->java Compiler中的Additional command line parameters添加-parameters参数即可
     * 也可以在类编译时加入 javac -parameters
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static List<String> getParameterNameByJava8(Class clazz, String methodName) {
        List<String> paramterList = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                Parameter[] params = method.getParameters();
                for (Parameter parameter : params) {
                    paramterList.add(parameter.getName());
                }

            }
        }
        return paramterList;
    }

    public static void getFields(Object object) {
//        Field fields[] = object.getClass().getDeclaredFields();
        Field fields[] = object.getClass().getFields();
        String[] name = new String[fields.length];
        Object[] value = new Object[fields.length];

        try {
            Field.setAccessible(fields, true);
            for (int i = 0; i < name.length; i++) {
                name[i] = fields[i].getName();
                value[i] = fields[i].get(object);
                System.out.println(name[i] + "->" + value[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String test(String param1, String param2) {
        String s = param1 + ":" + param2;
        System.out.println(s);
        return s;
    }
}

class PrivateClass2 {
    private String name = "zhangsan";

    public String getName() {
        return name;
    }

    private String sayHello(String name,Integer age) {
        return "Hello: " + name+",age = "+age;
    }
}

class Test {
    private String name = "aa";
    private int age = 18;
    public String gender = "male";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void test1() {
        System.out.println("test1");
    }

    private void test2() {
        System.out.println("test2");
    }
}

