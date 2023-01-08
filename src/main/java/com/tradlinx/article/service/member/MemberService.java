package com.tradlinx.article.service.member;

import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.front.MemberFront;
import com.tradlinx.article.model.mapper.MemberMapper;
import com.tradlinx.article.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberMapper mapper;
    private final MemberRepository memberRepository;

    public Member getMember(String userId) {
        return memberRepository.findByUserid(userId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 이용자입니다."));
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("존재하지 않는 이용자입니다."));
    }

    public Member findByMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 이용자입니다."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getMemberByUsername(username);
    }

    public void save(Member member) {
        memberRepository.save(member);
    }

    public boolean isExistMember(String userid) {
        return memberRepository.existsByUserid(userid);
    }

    public MemberFront toMemberFront(Member member) {
        return mapper.fromMember(member);
    }

    public void saveAll(List<Member> members) {
        memberRepository.saveAll(members);
    }
}
