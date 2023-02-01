package com.jungs.article.service.article;

import com.jungs.article.model.entity.Article;
import com.jungs.article.model.entity.Member;
import com.jungs.article.model.param.ArticleParam;
import com.jungs.article.repository.ArticleRepository;
import com.jungs.article.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public Article createArticle(ArticleParam param, Member member) {
        Article article = new Article();
        article.setTitle(param.getArticleTitle());
        article.setContents(param.getArticleContents());

        member.addPoint(3);
        article.setMember(member);
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
