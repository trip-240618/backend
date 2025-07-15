package com.ll.trip.global.security.filter.cloudfront;

import com.ll.trip.global.handler.exception.ServerException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CloudFrontCookieFilter extends OncePerRequestFilter {

    private final CloudFrontSignedCookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 인증된 사용자만 검증 시도
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키 추출
        Map<String, String> cloudFrontCookies = cookieUtil.extractCloudFrontCookies(request.getCookies());
        String policyBase64 = cloudFrontCookies.get("CloudFront-Policy");

        try {
            // 쿠키가 없거나 Policy가 없으면 무조건 재발급
            if (policyBase64 == null || cookieUtil.isCookieExpired(policyBase64)) {
                cookieUtil.setCookie(response);
            }
        } catch (InvalidKeySpecException e) {
            throw new ServerException("[CloudFront 쿠키 발급 실패] 키 스펙 오류: {}", e);
        } catch (IOException e) {
            throw new ServerException("IOException 발생", e);
        }

        filterChain.doFilter(request, response);
    }
}
