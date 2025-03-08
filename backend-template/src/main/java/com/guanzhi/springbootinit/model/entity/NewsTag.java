package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 新闻标签(NewsTag)实体类
 *
 * @author makejava
 * @since 2025-03-07 22:20:35
 */
@TableName(value = "news_tag")
@Data
public class NewsTag implements Serializable {
    private static final long serialVersionUID = -54275301314112171L;
    /**
     * id
     */
    private Long id;
    /**
     * 标签名称
     */
    private String tagname;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 更新时间
     */
    private Date updatetime;
    /**
     * 是否删除
     */
    private Integer isdelete;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }

}

