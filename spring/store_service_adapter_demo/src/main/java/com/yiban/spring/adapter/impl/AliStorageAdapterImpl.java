package com.yiban.spring.adapter.impl;

import com.yiban.spring.adapter.StorageAdapterI;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.log4j.Log4j2;

/**
 * @author david.duan
 * @packageName com.yiban.spring.adapter.impl
 * @className AliStorageAdapterImpl
 * @date 2025/4/17
 * @description 阿里云oss 具体实现逻辑
 */
@Log4j2
public class AliStorageAdapterImpl implements StorageAdapterI {
    @Override
    public void createBucket(String bucket) {
        System.out.println("aliyun");
    }

    @Override
    public void uploadFile(MultipartFile multipartFile, String bucket, String objectName) {
        //TODO
        System.out.println("uploadFile aliyun");
    }

    @Override
    public String getUrl(String bucket, String objectName) {
        return "aliyun";
    }
}
