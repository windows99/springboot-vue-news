package com.guanzhi.springbootinit.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.stereotype.Component;


/**
 * 获取验证码
 *
 * @author 观止
 */
@Component
public class CodeUtils {


    /**
     * 发送邮件代码
     *
     * @param targetEmail 目标用户邮箱
     * @param authCode    发送的验证码
     *   参考博客：https://blog.csdn.net/m0_66570338/article/details/128994951
     */
    public static String GetEmailCode(String targetEmail, String authCode) {
        try {
            // todo 需替换配置
            // 创建邮箱对象
            SimpleEmail mail = new SimpleEmail();
            // 设置发送邮件的服务器
            mail.setHostName("xxxxxxxxxxxxxxx");
            // "你的邮箱号"+ "上文开启SMTP获得的授权码"
            mail.setAuthentication("xxxxxxxxxxxxxxx", "xxxxxxxxxxxxxxx");
            // 发送邮件 "你的邮箱号"+"发送时用的昵称"
            mail.setFrom("xxxxxxxxxxxxxxx", "quick-develop-template");
            // 发送服务端口
            mail.setSslSmtpPort(String.valueOf(465));
            // 使用安全链接
            mail.setSSLOnConnect(true);
            System.setProperty("mail.smtp.ssl.enable", "true");
            System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
            // 接收用户的邮箱
            mail.addTo(targetEmail);
            // 邮件的主题(标题)
            mail.setSubject("验证码");
            // 邮件的内容
            mail.setMsg("【快速开发模板】您的验证码为:" + authCode + "(1分钟内有效)");
            // 发送
            mail.send();
            return "发送成功,请注意查收";
        } catch (EmailException e) {
            return e.getMessage();
        }
    }

}
