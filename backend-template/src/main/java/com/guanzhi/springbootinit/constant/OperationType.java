package com.guanzhi.springbootinit.constant;

/**
 * 操作类型常量
 */
public interface OperationType {
    // 通用操作
    String LOGIN = "LOGIN";                 // 登录
    String LOGOUT = "LOGOUT";               // 登出
    String REGISTER = "REGISTER";           // 注册
    String UPDATE_PROFILE = "UPDATE_PROFILE"; // 更新个人信息
    
    // 新闻相关操作
    String NEWS_ADD = "NEWS_ADD";           // 添加新闻
    String NEWS_UPDATE = "NEWS_UPDATE";     // 更新新闻
    String NEWS_DELETE = "NEWS_DELETE";     // 删除新闻
    String NEWS_PUBLISH = "NEWS_PUBLISH";   // 发布新闻
    String NEWS_SHELF = "NEWS_SHELF";       // 下架新闻
    String NEWS_VIEW = "NEWS_VIEW";         // 查看新闻
    
    // 标签相关操作
    String TAG_ADD = "TAG_ADD";             // 添加标签
    String TAG_UPDATE = "TAG_UPDATE";       // 更新标签
    String TAG_DELETE = "TAG_DELETE";       // 删除标签
    
    // 评论相关操作
    String COMMENT_ADD = "COMMENT_ADD";     // 添加评论
    String COMMENT_UPDATE = "COMMENT_UPDATE"; // 更新评论
    String COMMENT_DELETE = "COMMENT_DELETE"; // 删除评论
    
    // 用户管理操作
    String USER_ADD = "USER_ADD";           // 添加用户
    String USER_UPDATE = "USER_UPDATE";     // 更新用户
    String USER_DELETE = "USER_DELETE";     // 删除用户
    String USER_DISABLE = "USER_DISABLE";   // 禁用用户
    String USER_ENABLE = "USER_ENABLE";     // 启用用户
    
    // 系统操作
    String SYSTEM_CONFIG = "SYSTEM_CONFIG"; // 系统配置
    String DATA_BACKUP = "DATA_BACKUP";     // 数据备份
    String DATA_RESTORE = "DATA_RESTORE";   // 数据恢复
} 