package com.yiban.spring.service;

import com.yiban.spring.adapter.StorageAdapterI;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author david.duan
 * @packageName com.yiban.spring.service
 * @className FileService
 * @date 2025/4/17
 * @description FileService防腐层
 * 使用fileService(相当于domain防腐层)与adapter(相当于service层只做原子性操作)进行交互、Utils相当于dao层
 */
@Service
public class FileService {
    /**
     * 通过构造函数注入
     */
    private final StorageAdapterI storageAdapter;

    public FileService(StorageAdapterI storageAdapter) {
        this.storageAdapter = storageAdapter;
    }

    /**
     * 创建bucket
     *
     * @param bucket
     */
    public void createBucket(String bucket) {
        storageAdapter.createBucket(bucket);
    }

    /**
     * 上传图片、返回图片在minio的地址
     *
     * @param multipartFile
     * @param bucket
     * @param objectName
     */
    public String uploadFile(MultipartFile multipartFile, String bucket, String objectName) {
//        storageAdapter.uploadFile(multipartFile, bucket, objectName);
//        objectName = (StringUtils.isEmpty(objectName) ? "" : objectName + "/") + multipartFile.getOriginalFilename();
//        return storageAdapter.getUrl(bucket, objectName);
        return "";
    }
}
