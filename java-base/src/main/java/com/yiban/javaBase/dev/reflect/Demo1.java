package com.yiban.javaBase.dev.reflect;
import java.lang.reflect.*;

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


//        getFields(new Test(),false);
//        System.out.println("==============================");
//        getMethods(new Test(),false);
//        System.out.println("==============================");
//        System.out.println(getStaticFields("gender"));
        System.out.println("==============================");
        getPrivateParamConstructors(new Test());
        System.out.println("==============================");
        getPublicParamConstructors(new Test());
        System.out.println("==============================");
        System.out.println(Test.class.getSimpleName());
        System.out.println(Test.class.getCanonicalName());

//        PrivateClass2 p = new PrivateClass2();
//        Class<?> classType = p.getClass();
//
//        Field field = classType.getDeclaredField("name");
//        field.setAccessible(true); // 抑制Java对修饰符的检查
//        field.set(p, "lisi");
//        System.out.println(field.getName() + " " + field.get(p));
//        System.out.println("==============================");
//
//        /**
//         * getDeclaredMethod 表示获取私有方法 不加Declared表示获取public
//         */
//        Method method = classType.getDeclaredMethod("sayHello",
//                new Class[]{String.class, Integer.class});
//        method.setAccessible(true); // 抑制Java的访问控制检查
//        //调用对象的私有方法 要加上上面这句话
//        String str = (String) method.invoke(p, new Object[]{"zhangsan", 18});
//        System.out.println(str);
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

    /**
     * 获取对象的属性
     * @param object
     * @param isPublic
     */
    public static void getFields(Object object, boolean isPublic) {
        Field fields[];
        if (isPublic) {   //只能获取public字段
            fields = object.getClass().getFields();
        } else {
            //获取public和private字段
            fields = object.getClass().getDeclaredFields();
        }
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

    /**
     * 获取对象的方法
     * @param object
     * @param isPublic
     */
    private static void getMethods(Object object, boolean isPublic) {
        Method[] methods;
        if (isPublic){
            methods = object.getClass().getMethods();
        }else {
            methods = object.getClass().getDeclaredMethods();
        }
        String[] name = new String[methods.length];

        try {
            Field.setAccessible(methods, true);
            for (int i = 0; i < name.length; i++) {
                name[i] = methods[i].getName();
                System.out.println("name ->" + name[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 调用私有的有参构造函数
     * @param object
     * @throws Exception
     */
    private static void getPrivateParamConstructors(Object object) throws Exception {
        Class clazz = object.getClass();

        Class[] pTypes = new Class[]{int.class, Class.forName("java.lang.String")};
        Constructor constructor = clazz.getDeclaredConstructor(pTypes);
        constructor.setAccessible(true);
        Object[] objects = new Object[]{1, "aa"};
        constructor.newInstance(objects);
        //调用默认的构造函数
    }

    /**
     * 调用共有的有参构造函数
     * @param object
     * @throws Exception
     */
    private static void getPublicParamConstructors(Object object) throws Exception {
        Class clazz = object.getClass();
        Class[] pTypes = new Class[]{Class.forName("java.lang.String")};
        Constructor constructor = clazz.getConstructor(pTypes);
        Object[] objects = new Object[]{"aa"};
        constructor.newInstance(objects);
        //调用默认的构造函数
    }

    /**
     * 获取静态字段 其实完全没必要 因为使用对象也可以获取静态字段
     * @param field
     * @return
     */
    private static Object getStaticFields(String field){
        try {
            Field f = Test.class.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(new Test());
        } catch (Exception e) {}
        return null;
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

    private String sayHello(String name, Integer age) {
        return "Hello: " + name + ",age = " + age;
    }
}

class Test {
    public String gender = "male";
    private String name = "aa";
    private int age = 18;

    private static String str1;
    private static String str2  = "";

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

    private Test(int a,String b){
        System.out.println("private + parameters");
    }

    public Test(String name){
        System.out.println("public + parameters");
    }

    public Test(){
        System.out.println("public + non parameters");
    }
}


