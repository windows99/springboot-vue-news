package com.guanzhi.springbootinit.manager.file;


import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.manager.file.impl.MinIOManager;
import com.guanzhi.springbootinit.manager.file.impl.ObsManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 文件上传工厂
 */
@Component
public class FileManagerFactory {

    /**
     * 所使用的配置
     */
    @Value("${file.use}")
    private String use;

    /**
     * 自搭对象存储
     */
    @Resource
    private MinIOManager minIOManager;

    /**
     * 华为云对象存储
     */
    @Resource
    private ObsManager obsManager;

    /**
     * 创建文件上传实例
     *
     * @return
     */
    @Bean
    public FileManager fileManager() {
        FileManager fileManager;
        switch (use) {
            case "minio":
                fileManager =  minIOManager;
                break;
            case "obs" :
                fileManager = obsManager;
                break;
            default :
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传服务异常");
        }
        return fileManager;
    }

}
