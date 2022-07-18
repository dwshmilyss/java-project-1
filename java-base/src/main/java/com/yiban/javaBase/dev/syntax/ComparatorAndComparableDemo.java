package com.yiban.javaBase.dev.syntax;
import	java.util.Comparator;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Comparator and Comparable
 *
 * @auther WEI.DUAN
 * @date 2020/4/17
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ComparatorAndComparableDemo {

    //测试Comparable
    class Person implements Comparable<Person> {
        String name;
        int age;

        public Person(final String name, final int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + " = " + age;
        }

        @Override
        public int compareTo(@NotNull Person o) {
            //正序
//            return this.age - o.age;
            //逆序
            return o.age - this.age;
        }
    }

    /**
     * 测试Comparator
     */
    //定义实体类
    class Student {

        String name;
        int age;

        public Student(final String name, final int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return name + " = " + age;
        }
    }
    //定义一个外部比较器Comparator 逆序
    class StudentComparator implements Comparator<Student>{
        @Override
        public int compare(Student o1, Student o2) {
            return o2.age - o1.age;
        }
    }

    class PersonComparator implements Comparator<Person> {
        @Override
        public int compare(Person o1, Person o2) {
            return o1.age - o2.age;
        }
    }


    public static void main(String[] args) {
        List<Person> personList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Person person = new ComparatorAndComparableDemo().new Person("person"+i,i);
            personList.add(person);
        }
        Collections.shuffle(personList);
        Collections.sort(personList);
        personList.stream().forEach(System.out::println);
        System.out.println("=========");
        Set<Student> studentSet = new TreeSet<>(new ComparatorAndComparableDemo().new StudentComparator());
        for (int i = 1; i <= 10; i++) {
            Student student = new ComparatorAndComparableDemo().new Student("student"+i,i);
            studentSet.add(student);
        }
        studentSet.stream().forEach(System.out::println);
        System.out.println("=========");
        List<Student> studentList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Student student = new ComparatorAndComparableDemo().new Student("student"+i,i);
            studentList.add(student);
        }
        studentList.stream().forEach(System.out::println);
        Collections.sort(studentList,new ComparatorAndComparableDemo().new StudentComparator());
        studentList.stream().forEach(System.out::println);
        System.out.println("=========");
        Person[] persons = new Person[10] ;
        for (int i = 1; i <= 10; i++) {
            Person person = new ComparatorAndComparableDemo().new Person("person"+i,i);
            persons[i-1] = person;
        }
        Arrays.stream(persons).forEach(System.out::println);
        //注意 Arrays.sort只能对数组排序 不能对List排序
        // 数组中的元素必须实现Comparable接口
        // Arrays.sort也可以指定一个外部的比较器Comparator，如果同时指定了Comparable和Comparator
        //最终的排序结果以Comparator为准
        Arrays.sort(persons);
        Arrays.sort(persons,new ComparatorAndComparableDemo().new PersonComparator());
        Arrays.stream(persons).forEach(System.out::println);
    }
}
