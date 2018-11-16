package com.yiban.javaBase.dev.algorithm;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2018/11/15
 * @website http://blog.csdn.net/dwshmilyss
 */
public class FileSortDemo {

    public static void main(String[] args) {
        File file = new File("d:/pdi-ce-5.4.0.1-130.zip");
        System.out.println(file.length());
        //打印的是当前文件所在分区的剩余空间
        System.out.println(file.getFreeSpace());
        //所在硬盘总的容量（是整个硬盘 而非当前分区）
        System.out.println(file.getTotalSpace());
        //打印的是当前文件所在分区的可使用空间（和getFreeSpace一样）
        System.out.println(file.getUsableSpace());
    }

    /**
     * 递归获取某个目录下所有的文件
     * @param directoryPath
     * @param files
     * @return
     */
    public static List<File> getFiles(String directoryPath,List<File> files){
        File realFile = new File(directoryPath);
        if (realFile.isDirectory()){
            File[] subFiles = realFile.listFiles();
            for (File file : subFiles) {
                if (file.isDirectory()){
                    getFiles(file.getAbsolutePath(),files);
                }else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 按时间排序
     * @param path
     * @return
     */
    public static List<File> getFileSortByTime(String path){
        List<File> list = getFiles(path,new ArrayList<File>());
        if (list != null && list.size() > 0){
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    if (file1.lastModified() < file2.lastModified()){
                        return 1;
                    }else if (file1.lastModified() == file2.lastModified()){
                        return 0;
                    }else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }


    /**
     * 按文件大小排序
     * @param path
     * @return
     */
    public static List<File> getFileSortBySize(String path){
        List<File> list = getFiles(path,new ArrayList<File>());
        if (list != null && list.size() > 0){
            Collections.sort(list, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    if (file1.length() < file2.length()){
                        return 1;
                    }else if (file1.length() == file2.length()){
                        return 0;
                    }else {
                        return -1;
                    }
                }
            });
        }
        return list;
    }

    public static void test(){
        System.out.println("aaaa");
    }
}