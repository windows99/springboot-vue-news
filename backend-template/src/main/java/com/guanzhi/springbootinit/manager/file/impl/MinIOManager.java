package com.guanzhi.springbootinit.manager.file.impl;


import com.guanzhi.springbootinit.manager.file.FileManager;
import com.guanzhi.springbootinit.manager.file.config.MinIOClientConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;

/**
 * MiniIO 对象存储操作
 *
 * @author 观止
 */
@Component
public class MinIOManager implements FileManager {

    @Resource
    private MinIOClientConfig minIOClientConfig;

    @Resource
    private MinioClient minioClient;

    /**
     * 上传文件
     *
     * @param filepath 文件路径, 不包含服务器地址和端口号 格式: /${biz_name}/${user_id}/${uuid}.${file_type}
     * @param file     文件内容
     * @throws IOException 上传异常
     */
    @Override
    public void putObject(String filepath, File file) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            minioClient.putObject(PutObjectArgs.builder()
                    // 存储桶
                    .bucket(minIOClientConfig.getBucket())
                    // 文件名
                    .object(filepath)
                    // 文件内容
                    .stream(fileInputStream, -1, 5 * 1024 * 1024)
                    .build());
        }
    }

    /**
     * 文件绝对路径
     *
     * @param path 文件路径, 不包含服务器地址和端口号 格式: /${biz_name}/${user_id}/${uuid}.${file_type}
     * @throws IOException 移除失败
     */
    @Override
    public void removeObject(String path) throws IOException {
        try {
            minioClient.removeObject(
                    new RemoveObjectArgs.Builder()
                            .bucket(minIOClientConfig.getBucket())
                            .object(path)
                            .build()
            );
        } catch (Throwable e) {
            throw new IOException(e);
        }

    }

    @Override
    public void putObjectByBytes(String filepath, byte[] bytes) throws Exception {
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    // 存储桶
                    .bucket(minIOClientConfig.getBucket())
                    // 文件名
                    .object(filepath)
                    // 文件内容
                    .stream(inputStream, bytes.length, 5 * 1024 * 1024)
                    .build());
        }
    }

    @Override
    public String getPathPrefix() {
        return minIOClientConfig.getUrlPrefix() + "/" + minIOClientConfig.getBucket();
    }
}
