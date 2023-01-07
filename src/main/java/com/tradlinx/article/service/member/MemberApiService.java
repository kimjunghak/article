package com.tradlinx.article.service.member;

import com.tradlinx.article.config.JwtService;
import com.tradlinx.article.exception.UnAuthorizedException;
import com.tradlinx.article.model.dto.MemberFront;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.param.MemberParam;
import com.tradlinx.article.model.result.RestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberApiService {

    private final MemberService memberService;
    private final JwtService jwtService;

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
        Member member = memberService.getUser(param.getUserid());

        String rawPassword = param.getPw();
        String userPassword = member.getPassword();
        if (!passwordEncoder.matches(rawPassword, userPassword)) {
            throw new UnAuthorizedException("암호가 일치하지 않습니다.");
        }
        member.setLastLoggedAt(LocalDateTime.now());
        memberService.save(member);

        String jwt = jwtService.generateToken(member.getUserid());
        return RestResult.success(jwt);
    }

    public RestResult getProfile(HttpServletRequest request) {
        Member member = jwtService.getMember(request);
        MemberFront memberFront = memberService.toMemberFront(member);
        return RestResult.success(memberFront);
    }

    public RestResult getPoint(HttpServletRequest request) {
        Member member = jwtService.getMember(request);
        return RestResult.success(member.getPoint());
    }
}
