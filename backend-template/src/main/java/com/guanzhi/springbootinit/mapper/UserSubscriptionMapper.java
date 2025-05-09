package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.entity.UserSubscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户订阅 Mapper
 *
 * @author sk
 */
@Mapper
public interface UserSubscriptionMapper extends BaseMapper<UserSubscription> {

    /**
     * 获取用户的所有订阅标签
     *
     * @param userId 用户ID
     * @return 标签列表，包含ID和名称
     */
    List<Map<String, Object>> getUserSubscriptions(Long userId);

    /**
     * 获取用户订阅的标签
     */
    @Select("SELECT category FROM user_subscription WHERE userId = #{userId} AND status = 1 AND isDelete = 0")
    List<Long> getUserTags(@Param("userId") Long userId);
} 