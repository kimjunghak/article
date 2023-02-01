package com.jungs.article.controller;

import com.jungs.article.config.JwtService;
import com.jungs.article.model.front.MemberFront;
import com.jungs.article.model.param.ArticleParam;
import com.jungs.article.service.article.ArticleApiService;
import com.jungs.article.model.result.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final ArticleApiService articleApiService;
    private final JwtService jwtService;

    @PostMapping("")
    public RestResult createArticle(@RequestBody ArticleParam param, HttpServletRequest request) {
        MemberFront memberFront = jwtService.getAuthMember(request);
        return articleApiService.createArticle(param, memberFront);
    }

    @PutMapping("/{id}")
    public RestResult updateArticle(@PathVariable("id") Long articleId, @RequestBody ArticleParam param, HttpServletRequest request) {
        MemberFront authMember = jwtService.getAuthMember(request);
        return articleApiService.updateArticle(articleId, param, authMember);
    }

    @GetMapping("/{articleId}")
    public RestResult getArticle(@PathVariable Long articleId) {
        return articleApiService.getArticle(articleId);
    }

    @DeleteMapping("/{articleId}")
    public RestResult deleteArticle(@PathVariable Long articleId, HttpServletRequest request) {
        MemberFront authMember = jwtService.getAuthMember(request);
        return articleApiService.deleteArticle(articleId, authMember);
    }
}
