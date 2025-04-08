package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("news_push")
public class NewsPush {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long newsId;

    private Long userId;
    
    private Date pushTime;
    
    private Integer pushType;
    
    private Integer status;
    
    private Date createTime;
    
    private Date updateTime;
    
    @TableLogic
    private Integer isDelete;
}