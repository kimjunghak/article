package com.jungs.article.repository;

import com.jungs.article.model.entity.Article;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends BaseRepository<Article, Long> {

}
