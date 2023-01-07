package com.tradlinx.article.controller;

import com.tradlinx.article.model.param.ArticleParam;
import com.tradlinx.article.model.result.RestResult;
import com.tradlinx.article.service.article.ArticleApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final ArticleApiService articleApiService;

    @PostMapping("")
    public RestResult upsertArticle(@RequestBody ArticleParam param, HttpServletRequest request) {
        return articleApiService.upsertArticle(param, request);
    }

    @GetMapping("/{articleId}")
    public RestResult getArticle(@PathVariable Long articleId, HttpServletRequest request) {
        return articleApiService.getArticle(articleId, request);
    }

    @DeleteMapping("/{articleId}")
    public RestResult deleteArticle(@PathVariable Long articleId, HttpServletRequest request) {
        return articleApiService.deleteArticle(articleId, request);
    }
}
