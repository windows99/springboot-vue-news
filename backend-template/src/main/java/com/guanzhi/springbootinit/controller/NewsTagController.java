package com.guanzhi.springbootinit.controller;

import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.model.entity.NewsTag;
import com.guanzhi.springbootinit.service.NewsTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/new-tag")
@Slf4j
public class NewsTagController {

    @Autowired
    private NewsTagService newsTagService;

    @PostMapping
    public BaseResponse<?> addTag(@RequestBody NewsTag newsTag) {
        try {
            boolean result = newsTagService.addTag(newsTag);
            if (result) {
                return ResultUtils.success(result);
            } else {
                return ResultUtils.error(400, "Failed to add tag");
            }
        } catch (Exception e) {
            log.error("Failed to add news tag: {}", e.getMessage());
            return ResultUtils.error(500, "Failed to add tag");
        }
    }

    @DeleteMapping("/{id}")
    public BaseResponse<?> deleteTag(@PathVariable Long id) {
        try {
            boolean b = newsTagService.deleteTagById(id);
            return ResultUtils.success(b);
        } catch (Exception e) {
            return ResultUtils.error(500, "Failed to add tag");
        }
    }

    @PutMapping("/{id}")
    public BaseResponse<?> updateTag(@PathVariable Long id, @RequestBody NewsTag newsTag) {
        try {
            newsTag.setId(id);
            boolean b =  newsTagService.updateTag(newsTag);
            return ResultUtils.success(b);
        } catch (Exception e) {
            log.error("Update news tag error: {}", e.getMessage());
            return ResultUtils.error(500, "Failed to update tag");
        }
    }


    @GetMapping("/list")
    public BaseResponse<List<NewsTag>> getTagList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "100") Integer pageSize) {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("page", page);
        params.put("pageSize", pageSize);

        List<NewsTag> tagList = newsTagService.getTagList(params);
        return ResultUtils.success(tagList);
    }

    @GetMapping("/{id}")
    public BaseResponse<NewsTag> getTag(@PathVariable Long id) {
        NewsTag tag = newsTagService.getTagById(id);
        return ResultUtils.success(tag);

    }
}
