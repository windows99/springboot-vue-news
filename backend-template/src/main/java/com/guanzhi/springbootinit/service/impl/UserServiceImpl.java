package com.guanzhi.springbootinit.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.config.WxMiniConfig;
import com.guanzhi.springbootinit.constant.CommonConstant;
import com.guanzhi.springbootinit.constant.RegConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.model.dto.user.*;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.enums.UserRoleEnum;
import com.guanzhi.springbootinit.model.vo.LoginUserVO;
import com.guanzhi.springbootinit.model.vo.UserVO;
import com.guanzhi.springbootinit.service.UserService;
import com.guanzhi.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.guanzhi.springbootinit.constant.RedisKeyConstant.YZM_PRE;
import static com.guanzhi.springbootinit.constant.UserConstant.SALT;
import static com.guanzhi.springbootinit.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author sk

 * 

 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private WxMiniConfig wxMiniConfig;

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 获取参数
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String email = userRegisterRequest.getEmail();
        String checkCode = userRegisterRequest.getCheckCode();
        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(userPassword, checkPassword, email, checkCode)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }
        UserFormValid userFormValid = new UserFormValid();
        BeanUtils.copyProperties(userRegisterRequest, userFormValid);
        validFormValue(userFormValid);

        synchronized (email.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", email);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setEmail(email);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public boolean resetPassword(UserPasswordResetRequest userPasswordResetRequest) {
        String userPassword = userPasswordResetRequest.getUserPassword();
        String checkPassword = userPasswordResetRequest.getCheckPassword();
        String email = userPasswordResetRequest.getEmail();
        String checkCode = userPasswordResetRequest.getCheckCode();

        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(userPassword, checkPassword, email, checkCode)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }

        UserFormValid userFormValid = new UserFormValid();
        BeanUtils.copyProperties(userPasswordResetRequest, userFormValid);
        // 格式校验
        validFormValue(userFormValid);

        // 特殊性校验
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User loginUser = this.getOne(queryWrapper);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 修改数据
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setId(loginUser.getId());
        user.setUserPassword(encryptPassword);
        return this.updateById(user);
    }


    @Override
    public boolean updatePassword(UserPasswordUpdateRequest userPasswordUpdateRequest, HttpServletRequest request) {
        String oldPassword = userPasswordUpdateRequest.getOldPassword();
        String userPassword = userPasswordUpdateRequest.getUserPassword();
        String checkPassword = userPasswordUpdateRequest.getCheckPassword();
        String email = userPasswordUpdateRequest.getEmail();
        String checkCode = userPasswordUpdateRequest.getCheckCode();

        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(oldPassword, userPassword, checkPassword, email, checkCode)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }

        UserFormValid userFormValid = new UserFormValid();
        BeanUtils.copyProperties(userPasswordUpdateRequest, userFormValid);
        // 格式校验
        validFormValue(userFormValid);

        // 获取当前用户
        User loginUser = this.getById(this.getLoginUser(request).getId());
        // 判断旧密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        if (!loginUser.getUserPassword().equals(encryptPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确旧密码");
        }
        // 验证邮箱
        if (!loginUser.getEmail().equals(email)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入当前用户邮箱");
        }
        // 修改数据
        encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setId(loginUser.getId());
        user.setUserPassword(encryptPassword);
        return this.updateById(user);
    }


    @Override
    public LoginUserVO userLogin(String email, String userPassword, HttpServletRequest request) {

        // 1. 只需手动校验有无传递必填项
        if (StringUtils.isAnyBlank(email, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_NULL, "参数为空");
        }
        UserFormValid userFormValid = new UserFormValid();
        userFormValid.setUserPassword(userPassword);
        // 格式校验
        validFormValue(userFormValid);

        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.getOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, email cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    /**
     * 验证登录注册表单字段格式是否符合要求
     * 只校验传递的参数，不校验参数是否为必填项
     */
    public void validFormValue(UserFormValid userFormValid) {
        String userPassword = userFormValid.getUserPassword();
        String checkPassword = userFormValid.getCheckPassword();
        String email = userFormValid.getEmail();
        String checkCode = userFormValid.getCheckCode();

        // 密码校验
        if (userPassword != null) {
            if (userPassword.length() < 8 || userPassword.length() > 16) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码必须为8~16位");
            }
            boolean isMatches = Pattern.matches(RegConstant.PASSWORD_REG, userPassword);
            if (!isMatches) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不能含特殊字符");
            }
        }

        // 确认密码校验
        if (checkPassword != null) {
            // 密码和校验密码相同
            if (!checkPassword.equals(userPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
            }
        }

        // 校验邮箱
        if (email != null) {
            boolean isMatch = Pattern.matches(RegConstant.EMAIL_REG, email);
            if (!isMatch) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确邮箱");
            }
        }

        // 校验验证码,在邮箱不为null的情况下校验
        if (checkCode != null && email != null) {
            String code = redisTemplate.opsForValue().get(YZM_PRE + email);
            if (code == null || code.length() != 6) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "请重新获取验证码");
            }
            if (!checkCode.equals(code)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
            }
        }
    }

    @Override
    public LoginUserVO userLoginByWxMini(HttpServletRequest request, String code) {
        try {
            // 1.通过code、开发者appId，appSecret换取用户信息
            WxMaService wxmaService = wxMiniConfig.getWxMaService();
            WxMaJscode2SessionResult session = wxmaService.getUserService().getSessionInfo(code);
            String sessionKey = session.getSessionKey();
            String openId = session.getOpenid();
            log.info("登录用户openId:{}", openId);
            if (StringUtils.isAnyBlank(sessionKey, openId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            // 单机锁
            synchronized (openId.intern()) {
                // 获取代理对象（事务）
                UserService userService = (UserService) AopContext.currentProxy();
                return userService.registerOrLogin(request, openId);
            }
        } catch (Exception e) {
            log.error("userLoginByWxMini error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public LoginUserVO registerOrLogin(HttpServletRequest request, String openId) {
        // 查询用户是否已存在
        User user = this.lambdaQuery().eq(User::getOpenId, openId).one();
        if (user == null) {
            // 用户不存在，创建
            user = new User();
            user.setOpenId(openId);
            user.setUserName("默认昵称");
            // 插入默认主题
            boolean result = this.save(user);
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
            }
            log.info("新用户注册：{}", user);
        }
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public User getLoginUserPermitNull(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        long userId = currentUser.getId();
        return this.getById(userId);
    }


    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        Integer gender = userQueryRequest.getGender();
        String email = userQueryRequest.getEmail();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(gender != null, "gender", gender);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        System.out.println(queryWrapper);
        return queryWrapper;
    }
}
