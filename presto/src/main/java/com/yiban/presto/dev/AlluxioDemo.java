package com.yiban.presto.dev;

import alluxio.AlluxioURI;
import alluxio.client.file.FileInStream;
import alluxio.client.file.FileSystem;
import alluxio.exception.AlluxioException;

import java.io.IOException;

public class AlluxioDemo {
    public static void main(String[] args) throws IOException, AlluxioException {
        FileSystem fs = FileSystem.Factory.get();
        AlluxioURI path = new AlluxioURI("/LICENSE");
        FileInStream in = fs.openFile(path);
        byte[] buffer = new byte[1024];
        while (in.read(buffer) != -1) {
            System.out.println(new String(buffer));
        }
        in.close();
    }
}
