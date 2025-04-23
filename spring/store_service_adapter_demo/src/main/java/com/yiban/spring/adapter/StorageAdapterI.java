package com.yiban.spring.adapter;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author david.duan
 * @packageName com.yiban.spring.adapter
 * @interfaceName StorageAdapterI
 * @date 2025/4/17
 * @description 这是目标接口 **(目标抽象类，即客户需要的方法)**，我们想要的不同OSS都可通过该接口进行
 * 为了方便切换任何一个oss，我们将公共方法抽取为接口，由某个oss的实现类去编写具体逻辑
 */
public interface StorageAdapterI {
    /**
     * 创建bucket
     *
     * @param bucket
     */
    void createBucket(String bucket);

    /**
     * 上传文件
     *
     * @param multipartFile
     * @param bucket
     * @param objectName
     */
    void uploadFile(MultipartFile multipartFile, String bucket, String objectName);

    /**
     * 获取文件在oss中的url
     *
     * @param bucket
     * @param objectName
     * @return
     */
    String getUrl(String bucket, String objectName);
}
