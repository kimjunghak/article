package com.jungs.article.controller;

import com.jungs.article.config.JwtService;
import com.jungs.article.model.front.MemberFront;
import com.jungs.article.service.comment.CommentsApiService;
import com.jungs.article.model.param.CommentsParam;
import com.jungs.article.model.result.RestResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentsController {

    private final CommentsApiService commentsApiService;
    private final JwtService jwtService;

    @PostMapping("")
    public RestResult createComments(@RequestBody CommentsParam param, HttpServletRequest request) {
        MemberFront memberFront = jwtService.getAuthMember(request);
        return commentsApiService.createComments(param, memberFront);
    }

    @PutMapping("/{id}")
    public RestResult updateComments(@PathVariable("id") Long commentsId, @RequestBody CommentsParam param, HttpServletRequest request) {
        MemberFront authMember = jwtService.getAuthMember(request);
        return commentsApiService.updateComments(commentsId, param, authMember);
    }

    @DeleteMapping("/{id}")
    public RestResult deleteComments(@PathVariable("id") Long commentsId, HttpServletRequest request) {
        MemberFront authMember = jwtService.getAuthMember(request);
        return commentsApiService.deleteComments(commentsId, authMember);
    }
}
