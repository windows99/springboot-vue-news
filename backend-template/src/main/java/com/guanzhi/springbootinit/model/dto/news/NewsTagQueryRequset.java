package com.guanzhi.springbootinit.model.dto.news;


import com.guanzhi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NewsTagQueryRequset extends PageRequest {
    /**
     * id
     */
    private Long id;
    /**
     * 新闻标题（模糊查询）
     */
    private String tagname;

}
