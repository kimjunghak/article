package com.jungs.article.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jungs.article.exception.UnAuthorizedException;
import com.jungs.article.model.entity.Member;
import com.jungs.article.model.front.MemberFront;
import com.jungs.article.model.mapper.MemberMapper;
import com.jungs.article.model.properties.JwtProperties;
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
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final MemberMapper memberMapper;

    public String generateToken(Member member) {
        HashMap<String, Object> payloads = new HashMap<>();
        MemberFront memberFront = memberMapper.fromMember(member);
        payloads.put("member", memberFront);
        Claims claims = Jwts.claims(payloads);
        long expireTime = jwtProperties.getExpireTime();
        Date expire = Date.from(LocalDateTime.now().plusSeconds(expireTime).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(expire)
                .signWith(getSigningKey())
                .compact();
    }

    public String resolveJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private MemberFront parseJwt(String jwt) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build();

            Object member = jwtParser.parseClaimsJws(jwt).getBody().get("member");
            return new ObjectMapper().convertValue(member, MemberFront.class);
        } catch (Exception e) {
            throw new UnAuthorizedException("jwt 파싱 중 에러 발생", e);
        }
    }

    public boolean validateToken(String jwt) {
        try {
            parseJwt(jwt);

            return true;
        } catch (ExpiredJwtException e){
            throw new UnAuthorizedException("Expired jwt token ! ");
        } catch (JwtException e) {
            throw new UnAuthorizedException("Error on Token");
        }
    }

    public Authentication getAuthentication(String jwt) {
        MemberFront memberFront = parseJwt(jwt);
        return new UsernamePasswordAuthenticationToken(memberFront, jwt);
    }

    public MemberFront getAuthMember(HttpServletRequest request) {
        String jwt = resolveJwt(request);
        return parseJwt(jwt);
    }

    private Key getSigningKey() {
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(jwtProperties.getSecretKey());
        return new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}
