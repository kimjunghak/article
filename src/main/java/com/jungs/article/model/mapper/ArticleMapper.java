package com.jungs.article.model.mapper;

import com.jungs.article.model.entity.Article;
import com.jungs.article.model.entity.Comments;
import com.jungs.article.model.front.ArticleFront;
import com.jungs.article.model.front.CommentsFront;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    ArticleFront fromArticle(Article article);
}
