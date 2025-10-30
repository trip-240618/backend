package com.ll.trip.domain.user.oauth.service;

import com.ll.trip.domain.user.oauth.dto.OAuthTokenResponse;
import com.ll.trip.domain.user.oauth.dto.OAuthUserInfo;
import reactor.core.publisher.Mono;

public interface OAuthProviderService {
    // 인가 코드(code)로 AccessToken/IdToken 발급받기
    Mono<? extends OAuthTokenResponse> getToken(String code);

    // AccessToken(IdToken) 기반으로 사용자 정보 가져오기
    Mono<? extends OAuthUserInfo> getUserInfo(String token);
}
