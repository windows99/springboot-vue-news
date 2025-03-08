package com.guanzhi.springbootinit.manager.file.impl;


import com.guanzhi.springbootinit.manager.file.FileManager;
import com.guanzhi.springbootinit.manager.file.config.ObsClientConfig;
import com.obs.services.ObsClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Cos 对象存储操作
 *
 * @author 观止
 */
@Component
public class ObsManager implements FileManager {

    @Resource
    private ObsClientConfig obsClientConfig;

    @Resource
    private ObsClient obsClient;

    @Override
    public void putObject(String filepath, File file) throws IOException {
        obsClient.putObject(obsClientConfig.getBucket(),"dataImages" + filepath, Files.newInputStream(file.toPath()));
    }

    @Override
    public String getPathPrefix() {
        return obsClientConfig.getUrlPrefix() + "/dataImages";
    }

    @Override
    public void  deleteObject(String filepath) {
        System.out.println(filepath);
        obsClient.deleteObject(obsClientConfig.getBucket(),  filepath);
    }
}
