package com.tradlinx.article.model.front;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleFront {

    private Long articleId;

    private String articleTitle;

    private String articleContents;

    private int viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String memberName;

    private List<CommentsInfo> commentsContentsList;

    @Data
    public static class CommentsInfo {
        private String memberName;

        private String commentsContents;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;
    }

}
