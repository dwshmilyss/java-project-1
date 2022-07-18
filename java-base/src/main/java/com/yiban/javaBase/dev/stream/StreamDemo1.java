package com.yiban.javaBase.dev.stream;

import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * java8 stream demo
 *
 * @auther WEI.DUAN
 * @date 2017/11/13
 * @website http://blog.csdn.net/dwshmilyss
 */
public class StreamDemo1 {
    static final StopWatch stopWatch = new StopWatch();
    static List<Integer> myList = new ArrayList<>();

    public static void main(String[] args) {
        test3();
    }

    /**
     * 某些情况下，for循环的性能更好，所以不要用流随意代替for循环。
     * 但是一般来讲，并行流比普通流更高效
     */
    public static void test1() {
        for (int i = 0; i < 5000000; i++) {
            myList.add(i);
        }
        int result = 0;
        stopWatch.start();
        for (int i : myList
                ) {
            if (i % 2 == 0) {
                result += 1;
            }
        }
        stopWatch.stop();
        System.out.println(result);
        System.out.println("loop total time = " + stopWatch.getTotalTimeSeconds());
        stopWatch.start();
        System.out.println(myList.stream().filter(value -> value % 2 == 0).mapToInt(Integer::intValue).sum());
        stopWatch.stop();
        System.out.println("stream total time = " + stopWatch.getTotalTimeSeconds());
        stopWatch.start();
        System.out.println(myList.parallelStream().filter(value -> value % 2 == 0).mapToInt(Integer::intValue).sum());
        stopWatch.stop();
        System.out.println("Parallel Stream total time = " + stopWatch.getTotalTimeSeconds());
    }

    /**
     * 和spark的transform和action一样
     * 只有遇到action的操作时，计算才会被触发
     */
    public static void test2() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            employees.add(new Employee(i, "name_" + i));
        }
        Stream<String> employeeNameStreams = employees.parallelStream().filter(employee -> employee.id % 2 == 0)
                .map(employee -> {
//                    System.out.println("in map - " + employee.getName());
                    return employee.getName();
                });
        //延时2秒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("2 sec");
        stopWatch.start();
        //collect触发之前的操作
        employeeNameStreams.collect(Collectors.toList());
        stopWatch.stop();
        System.out.println("cost time = " + stopWatch.getTotalTimeMillis());
    }

    /**
     * 短路行为
     */
    public static void test3() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            employees.add(new Employee(i, "name_" + i));
        }
        Stream<String> employeeNameStreams = employees.parallelStream().filter(employee -> employee.id % 2 == 0)
                .map(employee -> {
//                    System.out.println("in map - " + employee.getName());
                    return employee.getName();
                });
        stopWatch.start();
        // 在这里，limit()方法在满足条件的时候会中断运行。 可以和test2()做对比
        employeeNameStreams.limit(100).collect(Collectors.toList());
        stopWatch.stop();
        System.out.println("cost time = " + stopWatch.getTotalTimeMillis());
    }

    static class Employee {
        int id;
        String name;

        public Employee(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }
    }
}
