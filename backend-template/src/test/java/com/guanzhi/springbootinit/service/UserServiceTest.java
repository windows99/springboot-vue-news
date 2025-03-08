package com.guanzhi.springbootinit.service;

import javax.annotation.Resource;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 *
 * @author 观止study
 *  @from https://blog.csdn.net/m0_66570338/article/details/132145086
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

}
