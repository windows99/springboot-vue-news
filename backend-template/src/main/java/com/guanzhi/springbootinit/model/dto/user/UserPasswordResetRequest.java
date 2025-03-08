package com.guanzhi.springbootinit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户重置密码请求体
 *
 * @author 观止
 */
@Data
public class UserPasswordResetRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userPassword;

    private String checkPassword;

    private String email;

    private String checkCode;
}
