package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.entity.UserOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户操作日志 Mapper
 *
 * @author sk
 */
@Mapper
public interface UserOperationLogMapper extends BaseMapper<UserOperationLog> {
} 