package com.guanzhi.springbootinit.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新个人信息请求
 *
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户性别
     */
    private Integer gender;

    /**
     * 简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}