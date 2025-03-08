package com.guanzhi.springbootinit.constant;

/**
 * @author 观止
 * redis缓存 key & timeout
 */
public interface RedisKeyConstant {

    /**
     * 验证码前缀
     */
    String YZM_PRE = "yzm:";
    /**
     * 验证码过期时间（s）
     */
    Integer YZM_TIME_OUT = 60;
}
