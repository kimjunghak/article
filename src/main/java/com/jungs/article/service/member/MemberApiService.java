package com.jungs.article.service.member;

import com.jungs.article.config.JwtService;
import com.jungs.article.exception.UnAuthorizedException;
import com.jungs.article.model.entity.Member;
import com.jungs.article.model.front.MemberFront;
import com.jungs.article.model.mapper.MemberMapper;
import com.jungs.article.model.param.MemberParam;
import com.jungs.article.model.result.RestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApiService {

    private final MemberService memberService;
    private final JwtService jwtService;

    private final MemberMapper memberMapper;

    private final PasswordEncoder passwordEncoder;

    public RestResult signup(MemberParam param) {
        boolean existMember = memberService.isExistMember(param.getUserid());
        if (existMember) {
            return RestResult.fail("이미 존재하는 아이디입니다.");
        }

        Member newMember = Member.builder()
                .userid(param.getUserid())
                .username(param.getUsername())
                .pw(passwordEncoder.encode(param.getPw()))
                .role("ROLE_USER")
                .build();

        memberService.save(newMember);
        return RestResult.success(newMember.getUsername());
    }


    public RestResult signin(MemberParam param) {
        Member member = memberService.getMember(param.getUserid());

        String rawPassword = param.getPw();
        String userPassword = member.getPassword();
        if (!passwordEncoder.matches(rawPassword, userPassword)) {
            throw new UnAuthorizedException("암호가 일치하지 않습니다.");
        }
        member.updateLastLoggedAt();
        memberService.save(member);

        String jwt = jwtService.generateToken(member);
        return RestResult.success(jwt);
    }

    public RestResult getProfile(HttpServletRequest request) {
        Member member = parseMember(request);
        MemberFront updateMember = memberMapper.fromMember(member);
        return RestResult.success(updateMember);
    }

    public RestResult getPoint(HttpServletRequest request) {
        Member member = parseMember(request);
        return RestResult.success(member.getPoint());
    }

    private Member parseMember(HttpServletRequest request) {
        String userid = jwtService.getAuthMember(request).getUserid();
        Member member = memberService.getMember(userid);
        return member;
    }
}
