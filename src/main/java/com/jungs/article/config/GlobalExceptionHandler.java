package com.jungs.article.config;

import com.jungs.article.exception.UnAuthorizedException;
import com.jungs.article.model.result.RestResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UnAuthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestResult unAuthorizedException(UnAuthorizedException e) {
        log.error("[ UnAuthorizedException ] 발생 !! {}", e.getMessage(), e);
        return RestResult.fail(e.getMessage());
    }

    @ExceptionHandler({NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestResult noSuchElementException(NoSuchElementException e) {
        log.error("[ NoSuchElementException ] 발생 !! {}", e.getMessage(), e);
        return RestResult.fail(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResult exception(Exception e) {
        log.error("[ {} ] 발생 !! {}", e.getClass().toString(), e.getMessage(), e);
        return RestResult.fail(e.getMessage());
    }
}
