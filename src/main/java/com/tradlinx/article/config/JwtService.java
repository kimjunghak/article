package com.tradlinx.article.config;

import com.tradlinx.article.exception.UnAuthorizedException;
import com.tradlinx.article.model.entity.Member;
import com.tradlinx.article.model.properties.JwtProperties;
import com.tradlinx.article.service.member.MemberService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private final MemberService memberService;

    public String generateToken(String userid) {
        long expireTime = jwtProperties.getExpireTime();
        Claims claims = Jwts.claims().setSubject(userid);
        Date expire = Date.from(LocalDateTime.now().plusSeconds(expireTime).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expire)
                .signWith(getSigningKey())
                .compact();
    }

    public String parseJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String parse(String jwt) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();

        return jwtParser.parseClaimsJws(jwt).getBody().getSubject();
    }

    public boolean validateToken(String jwt) {
        try {
            parse(jwt);

            return true;
        } catch (ExpiredJwtException e){
            throw new UnAuthorizedException("Expired jwt token ! ");
        } catch (JwtException e) {
            throw new UnAuthorizedException("Error on Token");
        }
    }

    public Authentication getAuthentication(String jwt) {
        String userid = parse(jwt);
        Member member = memberService.getUser(userid);
        return new UsernamePasswordAuthenticationToken(member, jwt);
    }

    public Member getMember(HttpServletRequest request) {
        String jwt = parseJwt(request);
        String userid = parse(jwt);
        return memberService.getUser(userid);
    }

    private Key getSigningKey() {
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(jwtProperties.getSecretKey());
        return new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}
