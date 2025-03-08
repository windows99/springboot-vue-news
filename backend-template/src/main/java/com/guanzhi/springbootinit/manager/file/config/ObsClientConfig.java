package com.guanzhi.springbootinit.manager.file.config;

import com.guanzhi.springbootinit.manager.file.domain.ClientProperty;
import com.obs.services.ObsClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 华为云对象存储客户端
 *
 * @author 观止
 */
@Configuration
@ConfigurationProperties(prefix = "file.obs.client")
public class ObsClientConfig extends ClientProperty {

    @Bean
    public ObsClient obsClient() {
        // 生成obs客户端
        return new ObsClient(getAccessKey(), getSecretKey(), getEndpoint());
    }
}