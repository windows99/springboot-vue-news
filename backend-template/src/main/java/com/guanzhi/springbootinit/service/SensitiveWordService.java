package com.guanzhi.springbootinit.service;

import com.guanzhi.springbootinit.model.entity.SensitiveWord;

import java.util.List;

public interface SensitiveWordService {

    List<SensitiveWord> getSensitiveWordList();

    boolean addSensitiveWord(SensitiveWord sensitiveWord);

    boolean updateSensitiveWord(SensitiveWord sensitiveWord);
//    List<String> getSensitiveWords();
//    boolean checkContentForSensitive(String content);

    boolean deleteSensitiveWord(Long id);

}
