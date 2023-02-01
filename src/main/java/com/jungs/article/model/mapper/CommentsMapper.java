package com.jungs.article.model.mapper;

import com.jungs.article.model.entity.Comments;
import com.jungs.article.model.front.CommentsFront;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentsMapper {

    CommentsMapper INSTANCE = Mappers.getMapper(CommentsMapper.class);

    CommentsFront fromComments(Comments comments);

}
