package com.ming.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author liuming
 * @description
 * @date 2023/1/12
 */
@Data
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioProp {
    private String endpoint;
    private String accesskey;
    private String secretKey;
}



