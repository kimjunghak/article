package com.jungs.article.model.entity;

import com.jungs.article.model.param.ArticleParam;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    private String articleTitle;

    private String articleContents;

    private int viewCount = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<Comments> commentsList = new ArrayList<>();

    public void setTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public void setContents(String articleContents) {
        this.articleContents = articleContents;
    }

    public void addViewCount() {
        this.viewCount++;
    }

    public void updateArticle(ArticleParam param) {
        this.articleTitle = param.getArticleTitle();
        this.articleContents = param.getArticleContents();
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
