package com.yiban.spring.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author david.duan
 * @packageName com.yiban.spring.config
 * @className MinioConfig
 * @date 2025/4/21
 * @description
 */
@Configuration
public class MinioConfig {
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("https://your-minio-server-url")
                .credentials("your-access-key", "your-secret-key")
                .build();
    }
}
