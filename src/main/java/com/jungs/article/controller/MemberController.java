package com.jungs.article.controller;

import com.jungs.article.model.param.MemberParam;
import com.jungs.article.model.result.RestResult;
import com.jungs.article.service.member.MemberApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class MemberController {

    private final MemberApiService memberApiService;

    @PostMapping("/signup")
    public RestResult signup(@RequestBody MemberParam param) {
        return memberApiService.signup(param);
    }

    @PostMapping("/signin")
    public RestResult signin(@RequestBody MemberParam param) {
        return memberApiService.signin(param);
    }

    @GetMapping("/profile")
    public RestResult getProfile(HttpServletRequest request) {
        return memberApiService.getProfile(request);
    }

    @GetMapping("/points")
    public RestResult getPoint(HttpServletRequest request) {
        return memberApiService.getPoint(request);
    }
}
