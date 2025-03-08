package com.guanzhi.springbootinit.utils;

import cn.hutool.core.io.FileTypeUtil;
import com.guanzhi.springbootinit.model.dto.file.CompressFile;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * @author: fansp
 * @date: 2025/2/3 22:16
 * @description 文件压缩工具类
 */

public class ImageCompressorUtils {
    /**
     * 压缩图片分辨率
     */
    private static final int MAX_DIMENSION = 2048;
    /**
     * 最大处理像素点
     * 系统能处理 16384*16384个像素点 在argb情况下占用500mb
     */
    private static final int MAX_READ_PIXEL = 16384 * 16384;
    /**
     * 输出图片质量
     */
    private static final float IMAGE_QUALITY = 0.8f;
    /**
     * 转换的格式
     */
    private static final String IMAGE_OUTPUT_TYPE = "webp";


    /**
     * 压缩图片并返回压缩后的输入流
     *
     * @param file 输入的图片
     * @return 压缩后的图片输入流；如果不支持该格式，返回原始输入流
     */

    public synchronized static CompressFile compressImage(File file) throws IOException {
        CompressFile compressFile = new CompressFile();
        compressFile.setFileType("webp");
        compressFile.setMimeType("image/webp");
        byte[] bytes = Files.readAllBytes(Paths.get(file.toURI()));
        compressFile.setContent(bytes);
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            // 检查图片分辨率, 防止OOM异常
            // 注意此方法会读取头部28个bytes，造成此流接下来读取时缺少部分bytes
            String extension = FileTypeUtil.getType(new ByteArrayInputStream(bytes));
            if (extension == null) {
                return compressFile;
            }
            // 使用 ImageReader 获取图像元数据
            Iterator<ImageReader> imageReadersBySuffix = ImageIO
                    .getImageReadersBySuffix(extension);
            if (!imageReadersBySuffix.hasNext()) {
                return compressFile;
            }
            // 可根据实际文件类型选择
            ImageReader imageReader = imageReadersBySuffix.next();
            imageReader.setInput(ImageIO.createImageInputStream(inputStream), false);

            // 检查是否是动态图
            int numImages = imageReader.getNumImages(true);
            if (numImages > 1 && ("gif".equals(extension) || "webp".equals(extension))) {
                // 动态图
                return compressFile;

            }
            // 获取图像的宽度和高度
            int width = imageReader.getWidth(0);
            int height = imageReader.getHeight(0);

            // 检查分辨率
            if ((long) height * (long) width > MAX_READ_PIXEL) {
                throw new IOException("图片文件分辨率过大");
            }

            BufferedImage image = imageReader.read(0);
            // 尝试降低分辨率
            if (!(width <= MAX_DIMENSION && height <= MAX_DIMENSION)) {
                // 尝试压缩图片到2k分辨率
                int newWidth, newHeight;

                if (width > height) {
                    newWidth = MAX_DIMENSION;
                    newHeight = (int) (height * ((double) MAX_DIMENSION / width));
                } else {
                    newHeight = MAX_DIMENSION;
                    newWidth = (int) (width * ((double) MAX_DIMENSION / height));
                }

                image = Thumbnails.of(image)
                        .size(newWidth, newHeight)
                        .asBufferedImage();
            }

            // 使用 Thumbnails 进行压缩
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Thumbnails.of(image)
                    // 保持原始尺寸
                    .scale(1.0)
                    // 设置压缩质量
                    .outputQuality(IMAGE_QUALITY)
                    .outputFormat(IMAGE_OUTPUT_TYPE)
                    .toOutputStream(byteArrayOutputStream);

            compressFile.setContent(byteArrayOutputStream.toByteArray());
            return compressFile;
        }

    }


}
