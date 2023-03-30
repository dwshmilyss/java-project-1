package com.yiban.javaBase.dev;

import scala.reflect.internal.Trees;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class StringDemo {
    public static String aaa ;

    public static String getAaa() {
        return aaa;
    }

    public static void main(String[] args) {
//        Timestamp timestamp = new Timestamp(1632471210000L);
//        System.out.println("timestamp = " + timestamp);
//        Path folder = Paths.get("/Users/edz/Desktop");
//        System.out.println("folder.getParent().toString() = " + folder.getParent().toString());
        StringDemo demo = new StringDemo();
        demo.aaa = "bbb";
        System.out.println("demo.aaa = " + demo.aaa);
        StringDemo demo1 = new StringDemo();
        System.out.println("demo1.aaa = " + demo1.aaa);

    }
}
