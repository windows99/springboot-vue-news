package com.guanzhi.springbootinit.controller;

import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.model.entity.SensitiveWord;
import com.guanzhi.springbootinit.service.SensitiveWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sensitive-word")
public class SensitiveWordController {

    @Autowired
    private SensitiveWordService sensitiveWordService;

    @GetMapping("/list")
    public BaseResponse<List<SensitiveWord>> getSensitiveWord() {
        List<SensitiveWord> SensitiveWordList = sensitiveWordService.getSensitiveWordList();
        return ResultUtils.success(SensitiveWordList);
    }


    @PostMapping("/add")
    public BaseResponse<?> addSensitiveWord(String word) {
        SensitiveWord sensitiveWord = new SensitiveWord();
        sensitiveWord.setWord(word);
        sensitiveWord.setStatus(0);
        sensitiveWordService.addSensitiveWord(sensitiveWord);
        return ResultUtils.success("添加成功");
    }

    @PutMapping("/{id}")
    public BaseResponse<?> updateSensitiveWord(@PathVariable Long id, @RequestBody SensitiveWord sensitiveWord) {
        try {
            sensitiveWord.setId(id);
            boolean b =  sensitiveWordService.updateSensitiveWord(sensitiveWord);
            return ResultUtils.success(b);
        } catch (Exception e) {
            return ResultUtils.error(500,"敏感词更新失败");
        }
    }
//
    @DeleteMapping("/delete/{id}")
    public BaseResponse<?> deleteSensitiveWord(@PathVariable Long id) {
        try {
            sensitiveWordService.deleteSensitiveWord(id);
            return ResultUtils.success("敏感词删除成功");
        } catch (Exception e) {
            return ResultUtils.error(500,"敏感词删除失败： " + e.getMessage());
        }
    }


}