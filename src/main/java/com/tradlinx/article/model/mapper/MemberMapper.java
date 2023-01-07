package com.tradlinx.article.model.mapper;

import com.tradlinx.article.model.front.MemberFront;
import com.tradlinx.article.model.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    MemberFront fromMember(Member member);
}
