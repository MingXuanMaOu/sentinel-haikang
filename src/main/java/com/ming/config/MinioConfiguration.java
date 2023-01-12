package com.ming.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuming
 * @description
 * @date 2023/1/12
 */
@Configuration
public class MinioConfiguration {
    @Autowired
    private MinioProp minioProp;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = MinioClient
                .builder()
                .endpoint(minioProp.getEndpoint())
                .credentials(minioProp.getAccesskey(), minioProp.getSecretKey())
                .build();
        return minioClient;
    }
}

