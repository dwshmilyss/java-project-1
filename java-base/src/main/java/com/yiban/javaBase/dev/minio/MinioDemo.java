package com.yiban.javaBase.dev.minio;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import io.minio.ObjectStat;
import io.minio.messages.Bucket;

import io.minio.MinioClient;
import io.minio.errors.MinioException;

public class MinioDemo {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException{
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
//            MinioClient minioClient = new MinioClient("xxxx", "xxx", "xxx");
//            MinioClient minioClient = new MinioClient("xxx", "xxx", "xxx");
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("xxx")
                            .credentials("xxx", "xxx")
                            .build();
            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("image-leadswarp");
//            boolean isExist = minioClient.bucketExists("minio-test");
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                System.out.println("Bucket not exists.");
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
//                minioClient.makeBucket("asiatrip");
            }
            // 列出所有存储桶
            List<Bucket> bucketList = minioClient.listBuckets();
            for (Bucket bucket : bucketList) {
                System.out.println(bucket.creationDate() + ", " + bucket.name());
            }

//            minioClient.statObject("image-leadswarp", "data/hudi/tenant3621/contact/.hoodie/hoodie.properties");
            ObjectStat objectStat = minioClient.statObject("image-leadswarp", "data/presto-auth.properties");
//            System.out.println("objectStat = " + objectStat);
            InputStream stream = minioClient.getObject("image-leadswarp", "data/presto-auth.properties");
            // 读取输入流直到EOF并打印到控制台。
//            byte[] buf = new byte[16384];
//            int bytesRead;
//            while ((bytesRead = stream.read(buf, 0, buf.length)) >= 0) {
//                System.out.println(new String(buf, 0, bytesRead));
//            }
            Properties authPP = new Properties();
            authPP.load(stream);
            Set<String> authKeySet  = authPP.stringPropertyNames();
            for (String authKey : authKeySet) {
                System.out.println("authKey = " + authKey);
            }
            // 关闭流，此处为示例，流关闭最好放在finally块。
            stream.close();
//            minioClient.statObject("image-leadswarp", "data/part-00000-97d66a16-717c-4028-a771-2ee722d76978-c000.csv");
//            // 使用putObject上传一个文件到存储桶中。
//            minioClient.putObject("image-leadswarp","test/", "/Users/edz/Desktop/core-site.xml");
////            minioClient.putObject("image-leadswarp","test", "/Users/edz/Downloads/conf");
//            System.out.println("/Users/edz/Desktop/111.pdf is successfully uploaded as asiaphotos.zip to `asiatrip` bucket.");
//            minioClient.getObject("image-leadswarp", "test/core-site.xml");

        } catch(Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
