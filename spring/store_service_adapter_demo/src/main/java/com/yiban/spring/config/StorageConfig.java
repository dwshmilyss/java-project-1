package com.yiban.spring.config;

import com.yiban.spring.adapter.StorageAdapterI;
import com.yiban.spring.adapter.impl.AliStorageAdapterImpl;
import com.yiban.spring.adapter.impl.MinioStorageAdapterImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author david.duan
 * @packageName com.yiban.spring.config
 * @className StorageConfig
 * @date 2025/4/17
 * @description
 * 此时如果想再加入一个新的OSS对象(得到xxUtils jar包等，我们无法进行修改)，只需新增一个xxadapter适配器类且在@Bean注解的方法中加一个else即可。
 * 注意：这里直接使用new的方式创建实现类(实现类也不需要使用@Service注解)，
 * 而不是先把所有的实现类通过注解定义出来，再直接返回对象
 * 这样如果新增一个OSS的话，不光要加else，还需再把实现类通过直接定义出来。所以这里只是一个简单的配置，如果想更灵活，还需要进一步优化
 */
@Configuration
@RefreshScope
public class StorageConfig {
    @Value("${storage.service.type}")
    private String storageType;

    @Bean
    @RefreshScope
    public StorageAdapterI storageAdapter() {
        if ("minio".equals(storageType)) {
            return new MinioStorageAdapterImpl();
        } else if("aliyun".equals(storageType)) {
            return new AliStorageAdapterImpl();
        } else{
            throw new IllegalArgumentException ("为找到对应的文件存储处理器");
        }
    }
}
