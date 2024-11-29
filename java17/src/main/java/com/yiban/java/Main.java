package com.yiban.java;

import java.io.IOException;
import java.util.zip.CRC32C;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) throws IOException {
        String checksumHex = Long.toHexString(calculateJava9CRC32C("/Users/edz/a.txt"));
        System.out.println(checksumHex);//56d6f602
        System.out.println("707a632d12a16d61b5bdc3e0830bb8f0".length());
    }

    public static long calculateJava9CRC32C(String filePath) throws IOException {
        CRC32C crc32c = new CRC32C();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[8192]; // 使用缓冲区优化读取
            int bytesRead;

            // 按块更新 CRC32C
            while ((bytesRead = fis.read(buffer)) != -1) {
                crc32c.update(buffer, 0, bytesRead);
            }
        }

        return crc32c.getValue();
    }
}