package com.tradlinx.article.model.mapper;

import com.tradlinx.article.model.entity.Article;
import com.tradlinx.article.model.entity.Comments;
import com.tradlinx.article.model.front.ArticleFront;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    ArticleMapper INSTANCE = Mappers.getMapper(ArticleMapper.class);

    @Mappings({
            @Mapping(target = "memberName", source = "member.username"),
            @Mapping(target = "commentsContentsList", source = "commentsList")
    })
    ArticleFront fromArticle(Article article);

    default List<ArticleFront.CommentsInfo> mapComments(List<Comments> commentsList) {
        ArrayList<ArticleFront.CommentsInfo> commentsInfos = new ArrayList<>();
        for (Comments comments : commentsList) {
            ArticleFront.CommentsInfo commentsInfo = new ArticleFront.CommentsInfo();
            commentsInfo.setMemberName(comments.getMember().getUsername());
            commentsInfo.setCommentsContents(comments.getCommentsContents());
            commentsInfo.setCreatedAt(comments.getCreatedAt());
            commentsInfo.setUpdatedAt(comments.getUpdatedAt());
            commentsInfos.add(commentsInfo);
        }
        return commentsInfos;
    }
}
