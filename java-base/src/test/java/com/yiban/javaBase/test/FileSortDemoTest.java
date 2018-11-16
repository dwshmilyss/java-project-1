package com.yiban.javaBase.test;

import com.yiban.javaBase.dev.algorithm.FileSortDemo;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2018/11/15
 * @website http://blog.csdn.net/dwshmilyss
 */
public class FileSortDemoTest {

    private static String parseDate(long timestamp){
        long time = timestamp / 1000;
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateStr = sdf.format(date);
        return dateStr;
    }

    @Test
    public void test(){
        FileSortDemo.test();
    }

    @Test
    public void testFileSortByTime(){
        List<File> files = FileSortDemo.getFileSortByTime("G:\\images");
        for (File file : files) {
            System.out.println(file.getAbsolutePath() + " " + parseDate(file.lastModified()));
        }
    }

    @Test
    public void testFileSortBySize(){
        List<File> files = FileSortDemo.getFileSortBySize("G:\\images");
        for (File file : files) {
            System.out.println(file.getAbsolutePath() + " " + file.length());
        }
    }
}