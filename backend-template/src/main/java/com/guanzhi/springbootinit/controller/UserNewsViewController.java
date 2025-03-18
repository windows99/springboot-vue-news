package com.guanzhi.springbootinit.controller;

import com.guanzhi.springbootinit.annotation.AuthCheck;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.constant.UserConstant;
import com.guanzhi.springbootinit.model.entity.UserNewsView;
import com.guanzhi.springbootinit.service.UserNewsViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/views")
public class UserNewsViewController {

    @Autowired
    private UserNewsViewService userNewsViewService;

    @PostMapping
    public BaseResponse<Boolean> recordView(@RequestParam Long userId, @RequestParam Long newsId, @RequestParam String newsTitle) {
        userNewsViewService.recordView(userId, newsId, newsTitle);
        return ResultUtils.success(true);
    }

    @GetMapping
    public BaseResponse<List<UserNewsView>> getViewHistory(@RequestParam Long userId, @RequestParam Map<String, Object> params) {
        List<UserNewsView> viewHistory = userNewsViewService.getUserViewHistory(userId, params);
        return ResultUtils.success(viewHistory);
    }

    @PostMapping("/{viewId}")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> deleteViewById(@PathVariable Long viewId,
                                                       @RequestParam(required = false) Long userId) {
         userNewsViewService.deleteViewById(viewId);
        return ResultUtils.success("删除成功");
    }

    @PostMapping("/all")
//    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> deleteAllViewsByUserId(@RequestParam Long userId) {
            userNewsViewService.deleteAllViewsByUserId(userId);
        return ResultUtils.success("删除成功");
    }

    @PostMapping("/admin/all")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> deleteAllViews() {
        userNewsViewService.deleteAllViews();
        return ResultUtils.success("删除成功");
    }

}