package com.yiban.javaBase.dev.tuning;
import	java.io.FileWriter;
import	java.io.BufferedWriter;
import	java.io.FileReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;


/**
 * Created by Administrator on 2018/3/7 0007.
 */
public class Tuning {
    public static void main(String[] args) throws Exception {
        String fileName = args[0];
        FileReader reader = new FileReader(fileName);
        BufferedReader br = new BufferedReader(reader);
        FileWriter writer = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(writer);
        String line = br.readLine();
        while (line != null) {
            System.out.println(line);
        }
        br.close();
    }

    /**
     * 在创建HashMap的时候预设初始大小
     */
    public static void test1(){

    }
}
