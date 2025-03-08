package com.guanzhi.springbootinit.model.dto.user;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户创建请求
 *
 * @author 观止study
 *  @from https://blog.csdn.net/m0_66570338/article/details/132145086
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户性别
     */
    private Integer gender;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}