package com.tradlinx.article.repository;

import com.tradlinx.article.model.entity.Member;

import java.util.Optional;

public interface MemberRepository extends BaseRepository<Member, Long> {

    Optional<Member> findByUserid(String userid);

    Optional<Member> findByUsername(String username);

    boolean existsByUserid(String userid);
}
