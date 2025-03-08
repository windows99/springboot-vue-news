package com.guanzhi.springbootinit.model.dto.user;

import lombok.Data;

/**
 * 通用账号密码邮箱校验体
 *
 * @author 观止
 */
@Data
public class UserFormValid {

    private String userPassword;

    private String checkPassword;

    private String email;

    private String checkCode;
}
