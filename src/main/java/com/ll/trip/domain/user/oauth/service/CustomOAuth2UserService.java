package com.ll.trip.domain.user.oauth.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.user.oauth.dto.SocialLoginDto;
import com.ll.trip.domain.user.user.dto.UserRegisterDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.domain.user.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final UserService userService;

	// 카카오톡 로그인이 성공할 때 마다 이 함수가 실행된다.
	@Override
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String providerTypeCode = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

		SocialLoginDto socialLoginDto = null;

		switch (providerTypeCode) {
			case "KAKAO":
				socialLoginDto = extractKakaoData(providerTypeCode, oAuth2User);
				break;
		}

		UserEntity user = whenSocialLogin(socialLoginDto);

		return new com.ll.trip.global.security.SecurityUser(
			user.getId(),
			user.getName(),
			user.getProviderId(),
			user.getPassword(),
			user.getProfileImg(),
			Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRoles()))
		);
	}

	//카카오 데이터 추출
	public SocialLoginDto extractKakaoData(String providerTypeCode, OAuth2User oAuth2User) {
		String oauthId = oAuth2User.getName();
		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map attributesProperties = (Map)attributes.get("properties");

		String nickname = (String)attributesProperties.get("nickname");
		String password = oauthId;
		String profileImageUrl = (String)attributesProperties.get("profile_image");
		String providerId = providerTypeCode + "__" + oauthId;

		return new SocialLoginDto(providerTypeCode, providerId, password, nickname, profileImageUrl);
	}

	@Transactional
	public UserEntity whenSocialLogin(SocialLoginDto socialLoginDto) {
		Optional<UserEntity> optUser = findByProviderId(socialLoginDto.getProviderId());

		if (optUser.isPresent())
			return optUser.get();

		UserRegisterDto registerDto = new UserRegisterDto(
			socialLoginDto.getNickname(),
			socialLoginDto.getProviderId(),
			socialLoginDto.getPassword(),
			socialLoginDto.getProfileImageUrl(),
			socialLoginDto.getProviderTypeCode()
		);

		return userService.register(registerDto);
	}

	public Optional<UserEntity> findByProviderId(String providerId) {
		return userRepository.findByProviderId(providerId);
	}
}

