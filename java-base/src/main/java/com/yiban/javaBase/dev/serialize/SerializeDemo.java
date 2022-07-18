package com.yiban.javaBase.dev.serialize;

import java.io.*;

/**
 * serialize demo
 *
 * 1、父类如果没有实现Serializable接口的话，则所有的字段都不会被序列化
 * 2、父类的私有字段不会被序列化
 * 3、如果父类实现了Serializable接口，则其子类自动也实现该接口
 * 4、当前类的所有成员都会被序列化（除非是静态字段和transient）
 * 5、transient修饰的字段的值无法被序列化，反序列化之后，会被设置为默认值（即int就会被设置为0，String就会被设置为"null"）
 * @auther WEI.DUAN
 * @date 2018/11/15
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SerializeDemo {
    public static void main(String[] args) throws Exception {

        //序列化对象
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./student.ser"));
        Student student = new Student("王麻子", "aaa","2018-10-10",10);
        out.writeObject(student);    //写入customer对象
        out.close();

        FileInputStream fis = new FileInputStream(new File("./student.ser"));
        ObjectInputStream in = new ObjectInputStream(fis);
        Student student1 = (Student) in.readObject();
        System.out.println(student1);
        fis.close();
        in.close();
    }
}


class Person implements Serializable{
    private static final long serialVersionUID = -772409167378876230L;

    public Person() {
    }

    public String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

class Student extends Person{

    private static final long serialVersionUID = 7815063267641151187L;

    private String grade;
    public String clazz;
    public int age;
    private static int gender = 1;
    public transient String birth;
    private transient int haha;


    public Student(String grade, String clazz, String birth, int haha) {
        super("dw",19);
        this.grade = grade;
        this.clazz = clazz;
        this.birth = birth;
        this.haha = haha;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", grade='" + grade + '\'' +
                ", clazz='" + clazz + '\'' +
                ", birth ='" + birth + '\'' +
                ", haha ='" + haha + '\'' +
                '}';
    }
}