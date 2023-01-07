package com.tradlinx.article.service.article;

import com.tradlinx.article.model.entity.Article;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.param.ArticleParam;
import com.tradlinx.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Article upsertArticle(ArticleParam param, Member member) {
        Long articleId = param.getArticleId();
        Article article;
        if (articleId == null) {
            article = new Article();
        } else {
            article = getArticle(articleId);
        }

        if (article.getArticleId() == null) {
            int currentPoint = member.getPoint();
            member.setPoint(currentPoint + 3);
            article.setMember(member);
        }
        article.setArticleTitle(param.getArticleTitle());
        article.setArticleContents(param.getArticleContents());

        return save(article);
    }

    public Article getArticle(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 글입니다."));
    }

    public Article save(Article article) {
        return articleRepository.save(article);
    }

    public void deleteArticle(Article article) {
        articleRepository.delete(article);
    }
}
