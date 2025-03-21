package com.guanzhi.springbootinit.constant;

/**
 * @author 观止
 * 正则表达式
 */
public interface RegConstant {
    /**
     * 邮箱格式校验
     */
    String EMAIL_REG = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";


    /**
     * 密码格式校验正则
     */
    String PASSWORD_REG = "^[0-9a-zA-Z]{6,16}$";
}
