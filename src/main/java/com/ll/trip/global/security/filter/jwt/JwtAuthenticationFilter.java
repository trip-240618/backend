package com.ll.trip.global.security.filter.jwt;

import com.ll.trip.global.security.filter.jwt.dto.ExtractedClaims;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value(value = "${jwt.token-secret}")
    private String tokenSecret;

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = jwtTokenUtil.resolveToken(request, "accessToken");
        String refreshToken = jwtTokenUtil.resolveToken(request, "refreshToken");
        Claims claims = null;

        boolean accessTokenVaild = accessToken != null && jwtTokenUtil.validateToken(accessToken);
        if (accessTokenVaild) {
            claims = jwtTokenUtil.getClaims(accessToken);
        } else if (refreshToken != null) {
            log.info("유효하지 않은 액세스토큰: " + accessToken);
            if (jwtTokenUtil.validateToken(refreshToken)) {
                claims = jwtTokenUtil.getClaims(refreshToken);
            } else {
                log.info("유효하지 않은 리프레시토큰: " + refreshToken);
                return;
            }
        } else {
            log.info("유효하지 않은 액세스토큰: " + accessToken);
            log.info("유효하지 않은 리프레시토큰: " + refreshToken);
            filterChain.doFilter(request, response);
            return;
        }

        ExtractedClaims exClaims = new ExtractedClaims(claims);

        Authentication auth = jwtTokenUtil.buildAuthentication(exClaims.getUserId(), exClaims.getUuid(),
                exClaims.getNickname(), exClaims.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);
        if (!accessTokenVaild) {
            String newAccessToken = jwtTokenUtil.createAccessToken(exClaims.getUserId(), exClaims.getUuid(),
                    exClaims.getNickname(), exClaims.getAuthorities());
            jwtTokenUtil.setTokenInCookie("accessToken", newAccessToken, response);
        }
        //log.info("유저 인증 성공 : " + exClaims.getUserId());
        filterChain.doFilter(request, response);
    }
}
