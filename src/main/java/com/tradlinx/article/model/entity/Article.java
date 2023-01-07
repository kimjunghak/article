package com.tradlinx.article.model.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
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

    @ManyToOne
    @JoinColumn(name = "memberId")
    @ToString.Exclude
    private Member member;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Comments> commentsList = new ArrayList<>();
}
