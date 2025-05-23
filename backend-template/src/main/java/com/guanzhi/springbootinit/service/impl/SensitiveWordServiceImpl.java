package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.SensitiveWordMapper;
import com.guanzhi.springbootinit.model.entity.SensitiveWord;
import com.guanzhi.springbootinit.service.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;


    @Override
    public boolean addSensitiveWord(SensitiveWord sensitiveWord) {
        sensitiveWordMapper.insert(sensitiveWord);
        return true;
    }


    @Override
    public List<SensitiveWord> getSensitiveWordList() {
        return baseMapper.selectList(null);
    }


    @Override
    public boolean updateSensitiveWord(SensitiveWord sensitiveWord) {
        try {
            sensitiveWordMapper.updateById(sensitiveWord);
            return true;
        } catch (Exception e) {
            log.error("更新敏感词失败:{}", sensitiveWord);
            throw new RuntimeException("Failed to update sensitive word from ", e);
        }
    }

    @Override
    public boolean deleteSensitiveWord(Long id) {
        try {
            int result = sensitiveWordMapper.deleteById(id);
            if (result == 0) {
                throw new RuntimeException("Failed to delete news tag with id: " + id);
            }
        } catch (Exception e) {
            log.error("Delete news tag error: ", e);
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean checkContentForSensitive(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        // 获取所有敏感词
        List<SensitiveWord> sensitiveWords = this.list();
        if (sensitiveWords.isEmpty()) {
            return false;
        }
        
        // 检查内容是否包含任何敏感词
        return sensitiveWords.stream()
                .anyMatch(word -> content.contains(word.getWord()));
    }

    @Override
    public String filterSensitiveWords(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        
        // 获取所有敏感词
        List<SensitiveWord> sensitiveWords = this.list();
        if (sensitiveWords.isEmpty()) {
            return content;
        }
        
        // 替换所有敏感词为 *
        String filteredContent = content;
        for (SensitiveWord word : sensitiveWords) {
            filteredContent = filteredContent.replace(word.getWord(), "*".repeat(word.getWord().length()));
        }
        
        return filteredContent;
    }
}