package com.guanzhi.springbootinit.model.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 敏感词表(SensitiveWord)实体类
 *
 * @author makejava
 * @since 2025-03-18 23:02:30
 */
public class SensitiveWord implements Serializable {
    private static final long serialVersionUID = 766844699310399828L;

    private Long id;
    /**
     * 敏感词
     */
    private String word;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 更新时间
     */
    private Date updatetime;
    /**
     * 状态：1-有效，0-无效
     */
    private Integer status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}

