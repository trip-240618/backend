package com.ll.trip.domain.user.oauth.dto;

public interface OAuthTokenResponse {
    String getAccessToken();
    String getRefreshToken();
    String getIdToken(); // 구글/애플에서만 필요, 카카오는 null 반환
}
