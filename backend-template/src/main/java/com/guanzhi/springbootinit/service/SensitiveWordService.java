package com.guanzhi.springbootinit.service;

import com.guanzhi.springbootinit.model.entity.SensitiveWord;

import java.util.List;

/**
 * 敏感词服务
 */
public interface SensitiveWordService {

    List<SensitiveWord> getSensitiveWordList();

    boolean addSensitiveWord(SensitiveWord sensitiveWord);

    boolean updateSensitiveWord(SensitiveWord sensitiveWord);

    boolean deleteSensitiveWord(Long id);

    /**
     * 检查内容是否包含敏感词
     *
     * @param content 待检查的内容
     * @return 是否包含敏感词
     */
    boolean checkContentForSensitive(String content);

    /**
     * 过滤敏感词
     *
     * @param content 待过滤的内容
     * @return 过滤后的内容
     */
    String filterSensitiveWords(String content);
}
