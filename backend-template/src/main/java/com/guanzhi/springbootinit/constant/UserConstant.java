package com.guanzhi.springbootinit.constant;

/**
 * 用户常量
 *
 * @author sk

 *  

 */
public interface UserConstant {


    /**
     * 盐值，混淆密码
     */
    String SALT = "guanzhi";

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";

    /**
     * 编辑角色
     */
    String EDITOR_ROLE = "editor";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    // endregion
}
