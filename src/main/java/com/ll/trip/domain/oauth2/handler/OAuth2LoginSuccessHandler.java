package com.ll.trip.domain.oauth2.handler;

import com.ll.trip.domain.user.user.service.UserService;
import com.ll.trip.global.security.userDetail.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService; // JWT 생성 및 관리 서비스 (직접 구현 필요)

    @Value("${oauth2.redirect.url}")
    private String targetUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException{

        // 1. 인증된 사용자 정보(Principal) 가져오기
        SecurityUser user = (SecurityUser) authentication.getPrincipal();

        // 2. 사용자 정보를 기반으로 JWT 토큰 생성 (Stateless)
        userService.createAndSetTokens(user.getId(), user.getUuid(), user.getNickname(), user.getAuthorities(), response);

        response.sendRedirect(targetUri);
    }
}
