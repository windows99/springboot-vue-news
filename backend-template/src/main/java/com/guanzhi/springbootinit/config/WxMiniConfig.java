package com.guanzhi.springbootinit.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 *
 * @author 观止
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "wx.miniapp")
@Data
public class WxMiniConfig {

    private String appId;

    private String appSecret;

    private WxMaService wxmaService;

    public WxMaService getWxMaService() {
        if (wxmaService != null) {
            return wxmaService;
        }
        synchronized (this) {
            if (wxmaService != null) {
                return wxmaService;
            }
            WxMaService maService = new WxMaServiceImpl();
            WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
            config.setAppid(appId);
            config.setSecret(appSecret);
            maService.setWxMaConfig(config);
            wxmaService = maService;
            return maService;
        }
    }

}