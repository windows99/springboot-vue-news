package com.guanzhi.springbootinit.manager.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


/**
 * 定义文件上传的接口，提高通用性
 * 之后我们的项目代码只调用接口，不调用具体的实现类，
 * 更换其他文件服务厂商时，就不用去修改名称了， 便于扩展。
 */
public interface FileManager {

    /**
     * 上传文件
     *
     * @param filepath 文件路径, 不包含服务器地址和端口号 格式: /${biz_name}/${user_id}/${uuid}.${file_type}
     * @param file     文件内容
     * @throws IOException 上传异常
     */
    default void putObject(String filepath, File file) throws Exception {
        throw new UnsupportedOperationException("方法未实现");
    }

    default void deleteObject(String filepath) throws Exception {
        throw new UnsupportedOperationException("方法未实现");
    }

    /**
     * 删除文件对象
     *
     * @param path 删除文件路径
     *             从bucket下一级开始
     *             eg: /dialog_image/1668904042690179073/Rq8GwZLo.webp
     */
    default void removeObject(String path) throws Exception {
        throw new UnsupportedOperationException("方法未实现");
    }

    /**
     * 将字节数组转换为对象，并将其保存到指定的文件路径中
     * 此方法用于处理字节数据的序列化操作，将字节数据写入到文件系统中
     * 由于方法体未实现，调用此方法将抛出UnsupportedOperationException异常
     *
     * @param filepath 文件保存的路径，不能为空
     * @param bytes 要保存的字节数组，不能为空
     * @throws Exception 抛出UnsupportedOperationException异常，表示方法未实现
     */
    default void putObjectByBytes(String filepath, byte[] bytes) throws Exception {
        throw new UnsupportedOperationException("方法未实现");
    }

    /**
     * 获取文件上传路径前缀
     * urlPrefix/bucket
     */
    default String getPathPrefix() {
        throw new UnsupportedOperationException("方法未实现");
    }

}
