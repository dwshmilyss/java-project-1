package com.yiban.spring.utils;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * @author david.duan
 * @packageName com.yiban.spring.spring_boot.dev.utils
 * @className MinioUtils
 * @date 2025/4/17
 * @description minio 工具类 (也是被适配的类)
 */
@Component
public class MinioUtils {
    @Autowired
    private MinioClient minioClient;

    /**
     * @param bucket: return void
     * @author david.duan on 2025/4/17 09:57
     * {@link }
     * @description 创建Bucket桶(文件夹目录)
     */
    public void createBucket(String bucket) throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!bucketExists) {//不存在就创建
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 上传文件
     * inputStream：处理文件的输入流
     * bucket：桶名称
     * objectName：桶中的对象名称，也就是上传后的文件在存储桶中的存储路径和文件名。
     * stream(inputStream：处理文件的输入流，-1：指定缓冲区大小的参数[-1为默认大小]， 5242889L：指定文件内容长度的上限)
     */
    public void uploadFile(InputStream inputStream, String bucket, String objectName) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName)
                .stream(inputStream, -1, 5242889L).build());
    }
}
