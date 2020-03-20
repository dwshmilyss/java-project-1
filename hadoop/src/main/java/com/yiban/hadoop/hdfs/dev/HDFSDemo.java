package com.yiban.hadoop.hdfs.dev;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

/**
 * @auther WEI.DUAN
 * @date 2020/3/15
 * @website http://blog.csdn.net/dwshmilyss
 */
public class HDFSDemo {
    public static FileSystem getFileSystem() throws Exception {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
//        FileSystem fs = FileSystem.get(URI.create("hdfs://master01:9000"), conf);
        return fs;
    }

    //读取
    public static void readFile(String fileName) throws Exception {
        FileSystem fs = getFileSystem();
        Path path = new Path(fileName);
        FileStatus fileStatus = fs.getFileStatus(path);
        System.out.println(fileStatus.getBlockSize());
        System.out.println(fileStatus.getLen());
        System.out.println(fileStatus.getPath());
        System.out.println(fileStatus.getOwner());
        BlockLocation[] blkLocations = fs.getFileBlockLocations(fileStatus,0, fileStatus.getLen());
        System.out.println("total block num:" + blkLocations.length);
        for (int i = 0; i < blkLocations.length; i++) {
            System.out.println(blkLocations[i].toString());
            System.out.println("文件在block中的偏移量" + blkLocations[i].getOffset()
                    + ", 长度" + blkLocations[i].getLength() + ",主机"+blkLocations[i].getCachedHosts().length + ",缓存主机:"+blkLocations[i].getHosts()[0] );
        }
        //在open()调用的时候会初始化DFSInputStream并在构造函数中调用openInfo()
        //openInfo()函数会获取LocatedBlocks的信息 LocatedBlocks类中有成员变量List<LocatedBlock> blocks，存储所有的block信息
        FSDataInputStream fsDataInputStream = fs.open(path);
        try {
            //DFSInputStream 继承 FSInputStream,  FSInputStream 继承 InputStream
            //所以copyBytes()最终会调用DFSInpuStream中的read函数
            IOUtils.copyBytes(fsDataInputStream, System.out, 4, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            IOUtils.closeStream(fsDataInputStream);
        }
    }


    //写入
    public static void writeFile(String fileName) throws Exception{
        FileSystem fileSystem = getFileSystem();
        Path path = new Path(fileName);
        FSDataOutputStream outputStream = fileSystem.create(path);
        FileInputStream inputStream = new FileInputStream(new File("d:/a.txt"));
        try {
            IOUtils.copyBytes(inputStream, outputStream, 1024, false);
        } finally {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

    public static void main(String[] args) {
        try {
            readFile("/a.txt");
//            writeFile("/b.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}