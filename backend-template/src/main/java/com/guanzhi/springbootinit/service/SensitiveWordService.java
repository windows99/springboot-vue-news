package com.guanzhi.springbootinit.service;

import java.util.List;

public interface SensitiveWordService {
    void addSensitiveWord(String word);
    void removeSensitiveWord(String word);
    void updateSensitiveWord(String oldWord, String newWord);
    List<String> getSensitiveWords();
    boolean checkContentForSensitive(String content);
}
