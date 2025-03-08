package com.guanzhi.springbootinit.model.dto.file;

import lombok.Data;

/**
 * @author: fansp
 * @date: 2025/2/3 22:15
 * @description 压缩文件
 */

@Data
public class CompressFile {
    /**
     * 文件内容
     */
    private byte[] content;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件mime类型
     */
    private String mimeType;
}
