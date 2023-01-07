package com.tradlinx.article.model.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentsParam {

    private Long commentsId;

    private Long articleId;

    private String commentsContents;
}
