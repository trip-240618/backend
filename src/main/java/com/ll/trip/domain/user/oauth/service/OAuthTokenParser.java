package com.ll.trip.domain.user.oauth.service;

import com.ll.trip.domain.user.oauth.dto.SocialUserInfo;
import com.ll.trip.domain.user.oauth.verifier.AppleTokenVerifier;
import com.ll.trip.domain.user.oauth.verifier.GoogleTokenVerifier;
import com.ll.trip.domain.user.oauth.verifier.TokenVerifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuthTokenParser {

    public static TokenVerifier getVerifier(String provider) {
        //왜 static? factory역할 (생성 + 반환)만을 하기 때문에 새로운 인스턴스가 필요하지 않음
        log.info(provider);
        return switch (provider.toLowerCase()) {
            case "google" -> new GoogleTokenVerifier();
            case "apple" -> new AppleTokenVerifier();
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    public boolean verifyToken(String provider, String idToken) {
        TokenVerifier verifier = getVerifier(provider);
        return verifier.verify(idToken);
    }

    public SocialUserInfo getUserInfo(String provider, String idToken) {
        TokenVerifier verifier = getVerifier(provider);

        // 1. 토큰 유효성 검증
        if (!verifier.verify(idToken)) {
            throw new SecurityException("ID Token 검증에 실패했습니다.");
        }

        // 2. 검증된 토큰에서 정보 추출
        return verifier.extractUserInfo(idToken);
    }
}
