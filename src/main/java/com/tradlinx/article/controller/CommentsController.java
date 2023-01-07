package com.tradlinx.article.controller;

import com.tradlinx.article.model.param.CommentsParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.article.CommentsApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentsController {

    private final CommentsApiService commentsApiService;

    @PostMapping("")
    public RestResult upsertComments(@RequestBody CommentsParam param, HttpServletRequest request) {
        return commentsApiService.upsertComments(param, request);
    }

    @DeleteMapping("/{commentsId}")
    public RestResult deleteComments(@PathVariable Long commentsId, HttpServletRequest request) {
        return commentsApiService.deleteComments(commentsId, request);
    }
}
