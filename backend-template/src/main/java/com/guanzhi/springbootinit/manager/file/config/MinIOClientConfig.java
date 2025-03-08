package com.guanzhi.springbootinit.manager.file.config;

import com.guanzhi.springbootinit.manager.file.domain.ClientProperty;
import io.minio.MinioClient;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * MinIO自搭建对象存储服务
 */
@Configuration
@ConfigurationProperties(prefix = "file.minio.client")
public class MinIOClientConfig extends ClientProperty {

    @Bean
    public MinioClient minioClient() throws NoSuchAlgorithmException, KeyManagementException {
        // 取消ssl认证(https下minio上传报错)
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };

        X509TrustManager x509TrustManager = (X509TrustManager) trustAllCerts[0];
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, x509TrustManager);
        builder.hostnameVerifier((s, sslSession) -> true);
        OkHttpClient okHttpClient = builder.build();

        // 创建 MinioClient 客户端
        return MinioClient.builder()
                .httpClient(okHttpClient)
                .endpoint(getEndpoint())
                .credentials(getAccessKey(), getSecretKey())
                .build();
    }

}