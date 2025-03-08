package com.guanzhi.springbootinit.manager.file.domain;

import lombok.Data;

/**
 * 对象存储配置字段
 */
@Data
public class ClientProperty {

    /**
     * 存储服务域名
     */
    private String endpoint;


    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 桶名
     */
    private String bucket;

    /***
     * url前缀
     */
    private String urlPrefix;
}
