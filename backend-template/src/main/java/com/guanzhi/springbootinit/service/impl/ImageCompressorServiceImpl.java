package com.guanzhi.springbootinit.service.impl;


import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.file.CompressFile;
import com.guanzhi.springbootinit.utils.ImageCompressorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ImageCompressorServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(ImageCompressorServiceImpl.class);

    public CompressFile tryCompressImage(File file) {
        try {
            return ImageCompressorUtils.compressImage(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片格式异常");
        }
    }

}
