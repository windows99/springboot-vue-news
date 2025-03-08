package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.dto.user.UserPasswordResetRequest;
import com.guanzhi.springbootinit.model.dto.user.UserPasswordUpdateRequest;
import com.guanzhi.springbootinit.model.dto.user.UserQueryRequest;
import com.guanzhi.springbootinit.model.dto.user.UserRegisterRequest;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.vo.LoginUserVO;
import com.guanzhi.springbootinit.model.vo.UserVO;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

/**
 * 用户服务
 *
 * @author 观止study
 *  @from https://blog.csdn.net/m0_66570338/article/details/132145086
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户重置密码
     *
     * @return 修改结果
     */
    boolean resetPassword(UserPasswordResetRequest userPasswordResetRequest);

    /**
     * 修改密码
     *
     * @param userPasswordUpdateRequest
     * @param request
     * @return
     */
    boolean updatePassword(UserPasswordUpdateRequest userPasswordUpdateRequest, HttpServletRequest request);

    /**
     * 用户登录
     *
     * @param email  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String email, String userPassword, HttpServletRequest request);

    /**
     * 用户登录（微信小程序）
     *
     * @param request
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLoginByWxMini(HttpServletRequest request, String code);

    /**
     * 用户登录or注册
     * @param request
     * @param openId
     * @return
     */
    LoginUserVO registerOrLogin(HttpServletRequest request, String openId);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param request
     * @return
     */
    User getLoginUserPermitNull(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    List<UserVO> getUserVO(List<User> userList);

    /**
     * 获取查询条件
     *
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

}
