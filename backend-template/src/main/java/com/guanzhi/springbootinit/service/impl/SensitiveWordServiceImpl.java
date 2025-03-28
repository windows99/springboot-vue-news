package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.SensitiveWordMapper;
import com.guanzhi.springbootinit.model.entity.SensitiveWord;
import com.guanzhi.springbootinit.service.SensitiveWordService;
import lombok.extern.slf4j.Slf4j;
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


//    @Override
//    public void removeSensitiveWord(String word) {
//        try {
/// /            if (!checkWordExists(word)) {
/// /                log.warn("敏感词不存在: {}", word);
/// /                return;
/// /            }
///
/// @return
//            SensitiveWord sensitiveWord = new SensitiveWord();
//            sensitiveWord.setWord(word);
//            sensitiveWord.setStatus(0);
//            sensitiveWordMapper.updateById(sensitiveWord);
//        } catch (Exception e) {
//            log.error("移除敏感词失败: {}", word, e);
//            throw new RuntimeException("Failed to remove sensitive word: " + word, e);
//        }
//    }
//
    @Override
    public boolean updateSensitiveWord(SensitiveWord sensitiveWord) {
        try {
            sensitiveWordMapper.updateById(sensitiveWord);
        } catch (Exception e) {
            log.error("更新敏感词失败:{}", sensitiveWord);
            throw new RuntimeException("Failed to update sensitive word from ", e);
        }
        return false;
    }

    @Override
    public boolean deleteSensitiveWord(Long id){
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
//
//    @Override
//    public List<String> getSensitiveWords() {
//        try {
//            List<SensitiveWord> sensitiveWordList = sensitiveWordMapper.selectList(null);
//            return sensitiveWordList.stream()
//                    .filter(word -> word.getStatus() == 1)
//                    .map(SensitiveWord::getWord)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            log.error("获取敏感词失败", e);
//            throw new RuntimeException("Failed to get sensitive words", e);
//        }
//    }
//
//    @Override
//    public boolean checkContentForSensitive(String content) {
//        try {
//            List<String> sensitiveWords = getSensitiveWords();
//            return !sensitiveWords.isEmpty() && sensitiveWords.parallelStream().anyMatch(word -> content.contains(word));
//        } catch (Exception e) {
//            log.error("检查内容是否包含敏感词失败", e);
//            return false;
//        }
//    }

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
