package com.guanzhi.springbootinit.service.impl;

import com.guanzhi.springbootinit.mapper.SensitiveWordMapper;
import com.guanzhi.springbootinit.model.entity.SensitiveWord;
import com.guanzhi.springbootinit.service.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SensitiveWordServiceImpl implements SensitiveWordService {

    @Autowired
    private SensitiveWordMapper sensitiveWordMapper;

    @Override
    public void addSensitiveWord(String word) {
        try {
//            if (checkWordExists(word)) {
//                log.warn("敏感词已经存在: {}", word);
//                return;
//            }
            SensitiveWord sensitiveWord = new SensitiveWord();
            sensitiveWord.setWord(word);
            sensitiveWord.setStatus(1);
            sensitiveWordMapper.insert(sensitiveWord);
        } catch (Exception e) {
            log.error("添加敏感词失败: {}", word, e);
            throw new RuntimeException("Failed to add sensitive word: " + word, e);
        }
    }

    @Override
    public void removeSensitiveWord(String word) {
        try {
//            if (!checkWordExists(word)) {
//                log.warn("敏感词不存在: {}", word);
//                return;
//            }
            SensitiveWord sensitiveWord = new SensitiveWord();
            sensitiveWord.setWord(word);
            sensitiveWord.setStatus(0);
            sensitiveWordMapper.updateById(sensitiveWord);
        } catch (Exception e) {
            log.error("移除敏感词失败: {}", word, e);
            throw new RuntimeException("Failed to remove sensitive word: " + word, e);
        }
    }

    @Override
    public void updateSensitiveWord(String oldWord, String newWord) {
        try {
            addSensitiveWord(newWord);
            removeSensitiveWord(oldWord);
        } catch (Exception e) {
            log.error("更新敏感词失败: 从 {} 到 {}", oldWord, newWord, e);
            throw new RuntimeException("Failed to update sensitive word from " + oldWord + " to " + newWord, e);
        }
    }

    @Override
    public List<String> getSensitiveWords() {
        try {
            List<SensitiveWord> sensitiveWordList = sensitiveWordMapper.selectList(null);
            return sensitiveWordList.stream()
                    .filter(word -> word.getStatus() == 1)
                    .map(SensitiveWord::getWord)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取敏感词失败", e);
            throw new RuntimeException("Failed to get sensitive words", e);
        }
    }

    @Override
    public boolean checkContentForSensitive(String content) {
        try {
            List<String> sensitiveWords = getSensitiveWords();
            return !sensitiveWords.isEmpty() && sensitiveWords.parallelStream().anyMatch(word -> content.contains(word));
        } catch (Exception e) {
            log.error("检查内容是否包含敏感词失败", e);
            return false;
        }
    }

//    private boolean checkWordExists(String word) {
//        try {
//            SensitiveWordExample example = new SensitiveWordExample();
//            example.createCriteria().andWordEqualTo(word);
//            return sensitiveWordMapper.countByExample(example) > 0;
//        } catch (Exception e) {
//            log.error("检查敏感词是否存在失败", e);
//            return false;
//        }
//    }
}
