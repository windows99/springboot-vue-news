package com.guanzhi.springbootinit.model.dto.news;

import com.guanzhi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewsQueryRequest extends PageRequest {
    /**
     * id
     */
    private Long id;
    /**
     * 新闻标题（模糊查询）
     */
    private String title;
    
    /**
     * 作者名称（模糊查询）
     */
    private String author;
    
    /**
     * 分类ID（精确匹配）
     */
    private Long category;


    /**
     * 状态（精确匹配）
     */
    private Long status;
}
