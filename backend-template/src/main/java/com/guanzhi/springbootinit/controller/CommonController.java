package com.guanzhi.springbootinit.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.constant.RedisKeyConstant;
import com.guanzhi.springbootinit.constant.RegConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.utils.CodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author 观止
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping(value = "/getMailCode")
    public BaseResponse<String> getCode(@RequestBody String targetEmail) {
        if (targetEmail == null) {
            throw new BusinessException(ErrorCode.PARAMS_NULL);
        }
        // 解析数据
        Gson gson = new Gson();
        targetEmail = gson.fromJson(targetEmail, new TypeToken<String>() {
        }.getType());

        // 判断格式是否正确
        boolean isMatch = Pattern.matches(RegConstant.EMAIL_REG, targetEmail);
        if (!isMatch) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请输入正确邮箱");
        }
        // 发送邮件
        String key = RedisKeyConstant.YZM_PRE + targetEmail;
        // 发送前先看下我们是否已经缓存了验证码
        String yzm = redisTemplate.opsForValue().get(key);
        String result = "请勿重复发送验证码";
        // 判断是否存在
        if (yzm == null) {
            // 随机生成六位数验证码
            String authCode = String.valueOf(new Random().nextInt(899999) + 100000);
            result = CodeUtils.GetEmailCode(targetEmail, authCode);
            // 存入redis中，设置有效期为1分钟
            redisTemplate.opsForValue().set(key, authCode, RedisKeyConstant.YZM_TIME_OUT, TimeUnit.SECONDS);
        }
        // 存在，直接返回，不再发送邮箱~
        return ResultUtils.success(result);
    }

}
