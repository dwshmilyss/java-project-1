package com.yiban.spring.adapter.impl;

import com.yiban.spring.adapter.StorageAdapterI;
import com.yiban.spring.utils.MinioUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import lombok.extern.log4j.Log4j2;

/**
 * @author david.duan
 * @packageName com.yiban.spring.adapter.impl
 * @className MinioStorageAdapterImpl
 * @date 2025/4/17
 * @description Minio相关操作的具体逻辑
 * <p>
 * Minio适配器类：通过继承或者组合方式，将被适配者类(minioUtils)的接口与目标抽象类的接口转换起来，
 * 使得客户端可以按照目标抽象类的接口进行操作。
 */
@Log4j2
public class MinioStorageAdapterImpl implements StorageAdapterI {

    @Override
    public void createBucket(String bucket) {
        System.out.println("minio");
    }

    @Override
    public void uploadFile(MultipartFile multipartFile, String bucket, String objectName) {
        //TODO
        System.out.println("uploadFile minio");
    }

    @Override
    public String getUrl(String bucket, String objectName) {
        return "minio";
    }
}
