package com.yiban.javaBase.dev.tuning;
import	java.io.PrintWriter;
import	java.io.BufferedWriter;
import	java.io.BufferedReader;
import java.io.FileNotFoundException;
import	java.io.FileReader;
import	java.io.File;

/**
 * Created by Administrator on 2018/3/7 0007.
 */
public class Tuning {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("");
        FileReader reader = null;
        reader = new FileReader(file);

        BufferedReader bufferedReader = null;

        BufferedWriter bufferedWriter = null;

        PrintWriter printWriter = null;
    }

    /**
     * 在创建HashMap的时候预设初始大小
     */
    public static void test1(){

    }
}
