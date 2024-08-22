package com.ll.trip.domain.user.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value(value = "${jwt.token-secret}")
    private String tokenSecret;


    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String accessToken = jwtTokenUtil.resolveToken(request, "accessToken");
        String refreshToken = jwtTokenUtil.resolveToken(request, "refreshToken");

        try {
            if (accessToken != null && jwtTokenUtil.validateToken(accessToken)) {
                Authentication auth = jwtTokenUtil.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                throw new JwtException("로그인을 안한 유저의 요청");
            }
        } catch (JwtException ex) {
            if (refreshToken != null && jwtTokenUtil.validateToken(refreshToken)) {
                String uuid = jwtTokenUtil.getUuid(refreshToken);
                String newAccessToken = jwtTokenUtil.createAccessToken(uuid, List.of("USER"));
                ResponseCookie newAccessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                        .httpOnly(true)
                        .path("/")
                        .secure(true)
                        .build();

                response.addHeader("Set-Cookie", newAccessTokenCookie.toString());


                Authentication auth = jwtTokenUtil.getAuthentication(newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                // Here you can add some response to the client about no token
                logger.info("로그인을 안한 유저의 요청");
            }
        }

        filterChain.doFilter(request, response);
    }
}
