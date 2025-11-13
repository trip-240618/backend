package com.ll.trip.domain.oauth2.service;

import com.ll.trip.domain.oauth2.OAuthAttributes;
import com.ll.trip.domain.user.oauth.service.OAuth2Service;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.global.security.userDetail.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository; // 사용자 DB 연동을 위한 예시
    private final OAuth2Service oAuth2Service;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 OAuth2UserService 구현체 호출
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // 2. 서비스 제공자 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google, kakao, apple 중 하나
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // 각 서비스의 사용자 식별 필드 이름 (sub, id 등)

        // 3. 서비스별 사용자 정보 추출을 위한 추상화
        OAuthAttributes attributes = OAuthAttributes.of(registrationId,
                userNameAttributeName,
                oauth2User.getAttributes());

        UserEntity user = oAuth2Service.whenLogin(attributes, registrationId);

        // 5. Spring Security Principal로 반환
        return new SecurityUser(
                user.getId(),
                user.getUuid(),
                user.getNickname(),
                user.getAuthorities()
        );
    }

// private User saveOrUpdate(OAuthAttributes attributes) { ... }
}