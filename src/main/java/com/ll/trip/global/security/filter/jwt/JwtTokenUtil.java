package com.ll.trip.global.security.filter.jwt;

import com.ll.trip.global.security.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {

    @Value(value = "${jwt.token-secret}")
    private String tokenSecret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Value(value = "${jwt.access-token-time}")
    private long accessTokenValidityInMilliseconds;

    @Value(value = "${jwt.refresh-token-time}")
    private long refreshTokenValidityInMilliseconds;

    private final UserDetailsServiceImpl userDetailsService;

    public String createRefreshToken(long userId, String uuid, String nickname,
                                     Collection<? extends GrantedAuthority> authorities) {
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)  // 권한 정보(ROLE_XXX) 추출
                .collect(Collectors.toList());
        return getString(userId, uuid, nickname, roles, refreshTokenValidityInMilliseconds);
    }

    public String createAccessToken(long userId, String uuid, String nickname,
                                    Collection<? extends GrantedAuthority> authorities) {
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)  // 권한 정보(ROLE_XXX) 추출
                .collect(Collectors.toList());
        return getString(userId, uuid, nickname, roles, accessTokenValidityInMilliseconds);
    }

    public void setTokenInCookie(String tokenKey, String accessToken, HttpServletResponse response) {
        ResponseCookie accessTokenCookie = ResponseCookie.from(tokenKey, accessToken)
                .httpOnly(true)
                .path("/")
                .secure(true)
                .sameSite("None")
                .build();


        response.addHeader("Set-Cookie", accessTokenCookie.toString());
    }

    private String getString(long id, String uuid, String nickname, List<String> roles,
                             long tokenValidityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(uuid);
        claims.put("id", id);  // 사용자 ID 추가
        claims.put("nickname", nickname);  // 닉네임 추가
        claims.put("roles", roles);

        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication buildAuthentication(long id, String uuid, String nickname,
                                              Collection<? extends GrantedAuthority> authorities) {
        UserDetails userDetails = userDetailsService.buildUserByClaims(id, uuid, nickname, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.info("잘못된 구조의 토큰: " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.info("서명이 유효하지 않은 토큰: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.info("잘못된 입력이나 null 값: " + e.getMessage());
            return false;
        }

    }

    public String resolveToken(HttpServletRequest req, String tokenName) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;

    }

    public String resolveWebSocketToken(ServerHttpRequest req, String tokenName) {
        if (req instanceof org.springframework.http.server.ServletServerHttpRequest) {
            jakarta.servlet.http.HttpServletRequest servletRequest =
                    ((org.springframework.http.server.ServletServerHttpRequest) req).getServletRequest();
            jakarta.servlet.http.Cookie cookie = WebUtils.getCookie(servletRequest, tokenName);
            if (cookie != null) {
                return cookie.getValue();
            }
        } else {
            // HttpServletRequest로 변환할 수 없는 경우, HttpHeaders에서 직접 "Cookie" 헤더를 파싱
            List<String> cookieHeaders = req.getHeaders().get(HttpHeaders.COOKIE);
            if (cookieHeaders != null) {
                for (String cookieHeader : cookieHeaders) {
                    // "cookieHeader"는 "cookie1=value1; cookie2=value2" 형태
                    String[] cookies = cookieHeader.split("; ");
                    for (String cookiePair : cookies) {
                        String[] parts = cookiePair.split("=", 2);
                        if (parts.length == 2 && tokenName.equals(parts[0])) {
                            return parts[1];
                        }
                    }
                }
            }
        }
        return null;
    }

}
