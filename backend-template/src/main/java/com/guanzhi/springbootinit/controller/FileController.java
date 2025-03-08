package com.guanzhi.springbootinit.controller;

// Hutool工具类导入
import cn.hutool.core.io.FileUtil;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.manager.file.FileManager;
import com.guanzhi.springbootinit.model.dto.file.CompressFile;
import com.guanzhi.springbootinit.model.dto.file.UploadFileRequest;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.enums.FileUploadBizEnum;
import com.guanzhi.springbootinit.service.UserService;
import com.guanzhi.springbootinit.service.impl.ImageCompressorServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * 文件接口
 */
@RestController  // 声明为RESTful控制器
@RequestMapping("/file")  // 基础路径映射
@Slf4j  // 启用日志
public class FileController {  // 文件上传控制器

    @Resource  // 注入用户服务
    private UserService userService;

    @Resource  // 注入文件管理服务
    private FileManager fileManager;

    @Resource  // 注入图片压缩服务
    private ImageCompressorServiceImpl imageCompressorServiceImpl;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    // 文件上传接口
    @PostMapping("/upload")  // POST请求映射
    public BaseResponse<String> uploadFile(
            @RequestPart("file") MultipartFile multipartFile,  // 接收上传的文件
            UploadFileRequest uploadFileRequest,  // 上传请求参数
            HttpServletRequest request) {  // HTTP请求对象
        // 业务类型校验
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {  // 无效的业务类型
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 文件校验（大小、类型等）
        validFile(multipartFile, fileUploadBizEnum);
        
        // 获取登录用户信息
        User loginUser = userService.getLoginUser(request);
        
        // 生成唯一文件名：8位随机字符串 + 原始文件名
        String uuid = RandomStringUtils.randomAlphanumeric(8);  // 生成8位随机字符串
        String filename = uuid + "-" + multipartFile.getOriginalFilename();  // 拼接唯一文件名
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);  // 构建文件存储路径
        
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(filepath, null);
            // 将上传文件转存到临时文件
            multipartFile.transferTo(file);
            
            // 尝试压缩图片文件
//            CompressFile compressFile = imageCompressorServiceImpl.tryCompressImage(file);
            fileManager.putObject(filepath, file);
//            if (compressFile != null) {  // 压缩成功则上传压缩后的内容
//                fileManager.putObjectByBytes(filepath, compressFile.getContent());
//            } else {  // 无需压缩则直接上传原文件
//                fileManager.putObject(filepath, file);
//            }
            
            // 返回完整的文件访问地址
            return ResultUtils.success(fileManager.getPathPrefix() + filepath);
        } catch (Exception e) {  // 异常处理
            log.error("文件上传失败, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {  // 最终清理
            if (file != null) {
                // 删除临时文件（无论上传成功与否）
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    // 文件校验方法
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 获取文件基本信息
        long fileSize = multipartFile.getSize();  // 文件大小（字节）
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());  // 文件扩展名
        
        final long ONE_M = 1024 * 1024L;  // 1MB大小限制
        
        // 用户头像业务校验
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            // 文件大小校验（不超过1MB）
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            // 文件类型白名单校验
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }


//    Putting
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteFile(
            @RequestParam String fileKey,
            HttpServletRequest request) {
        try {
            fileManager.deleteObject(fileKey);
            return ResultUtils.success("File deleted successfully");
        } catch (Exception e) {
            log.error("Failed to delete file: " + fileKey, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to delete file");
        }
    }

}
