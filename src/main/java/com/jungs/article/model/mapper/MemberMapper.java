package com.jungs.article.model.mapper;

import com.jungs.article.model.entity.Member;
import com.jungs.article.model.front.MemberFront;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberFront fromMember(Member member);

    Member toMember(MemberFront memberFront);
}
