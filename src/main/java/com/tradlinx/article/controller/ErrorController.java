package com.tradlinx.article.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping({"/error", "/error-page"})
    public String handleError() {
        log.error("error 발생");
        return "redirect:/";
    }
}
