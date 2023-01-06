package com.tradlinx.article.config;

import com.tradlinx.article.exception.UnAuthorizedException;
import com.tradlinx.article.model.entity.User;
import com.tradlinx.article.service.user.UserService;
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

    private final UserService userService;

    public String generateToken(String userid) {
        long expireTime = 2 * 24 * 60 * 1000L;
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
        String jwt = null;
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            jwt = bearerToken.substring(7);
        }

        if (jwt == null) {
            throw new UnAuthorizedException("허용되지 않은 접근입니다.");
        }

        return parse(jwt);
    }

    private String parse(String jwt) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();

        return jwtParser.parseClaimsJwt(jwt).getBody().getSubject();
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

    public Authentication jwtAuthentication(String jwt) {
        String userid = parse(jwt);
        User user = getUser(userid);
        return new UsernamePasswordAuthenticationToken(user, jwt);
    }

    public User getUser(String subject) {
        return userService.getUser(subject);
    }

    private Key getSigningKey() {
        String secretKey = "tradLinx";
        byte[] secretKeyBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public Authentication getAuthentication(String jwt) {
        return null;
    }
}
