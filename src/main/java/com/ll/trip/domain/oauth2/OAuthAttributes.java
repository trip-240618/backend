package com.ll.trip.domain.oauth2;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;
    private final String oauthId; // ✅ 새로 추가된 OAuth ID 필드

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey,
                           String name, String email, String picture, String oauthId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.oauthId = oauthId; // ✅ 생성자에도 추가
    }

    // --- 정적 팩토리 메서드 ---

    /**
     * @param registrationId 소셜 서비스 이름 (google, kakao, apple)
     * @param userNameAttributeName 사용자 식별자 키 이름 (sub, id 등)
     * @param attributes 소셜 서비스가 제공한 사용자 속성
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes) {

        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        // 구글과 애플은 OIDC 표준을 따르므로 유사하게 처리 가능 (Google을 기본으로 가정)
        return ofGoogle(userNameAttributeName, attributes);
    }

    // --- 서비스별 파싱 메서드 ---

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        // 구글과 애플은 OIDC 표준에 따라 'sub' 키를 사용자 고유 ID로 사용합니다.
        String id = String.valueOf(attributes.get(userNameAttributeName));

        return OAuthAttributes.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .picture(String.valueOf(attributes.get("picture")))
                .oauthId(id) // ✅ 'sub' (또는 userNameAttributeName) 키의 값을 OauthId로 저장
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오는 사용자 정보가 "kakao_account" 내부에 중첩되어 있음
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name(String.valueOf(profile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .picture(String.valueOf(profile.get("profile_image_url")))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // Apple 파싱 로직은 별도의 ID Token 파싱 과정이 필요하므로 구현이 복잡할 수 있습니다.
    // 여기서는 Google과 유사하게 단순 파싱하는 예시만 포함합니다.

}
