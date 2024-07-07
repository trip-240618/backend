package com.ll.trip.domain.user.oauth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ll.trip.domain.user.jwt.JwtTokenUtil;
import com.ll.trip.domain.user.oauth.dto.KakaoPropertiesDto;
import com.ll.trip.domain.user.oauth.dto.KakaoTokenResponseDto;
import com.ll.trip.domain.user.oauth.dto.KakaoUserInfoDto;
import com.ll.trip.domain.user.user.entity.RefreshToken;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.domain.user.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2Service {

	private final UserService userService;
	private final UserRepository userRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final WebClient webClient;
	private static final String TOKEN_URI = "https://kauth.kakao.com/oauth/token";
	private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";
	private static final String GRANT_TYPE = "authorization_code";

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;

	public Mono<KakaoTokenResponseDto> getToken(String code) {
		String uri = TOKEN_URI + "?grant_type=" + GRANT_TYPE + "&client_id=" + clientId + "&redirect_uri=" + redirectUri
					 + "/test&code=" + code;

		return webClient.get()
			.uri(uri)
			.retrieve()
			.bodyToMono(KakaoTokenResponseDto.class);

	}

	public Mono<KakaoUserInfoDto> getUserInfo(String token) {
		String uri = USER_INFO_URI;

		return webClient.get()
			.uri(uri)
			.header("Authorization", "Bearer " + token)
			.retrieve()
			.bodyToFlux(KakaoUserInfoDto.class)
			.next(); // Flux 스트림의 첫 번째 항목을 반환
	}

	public UserEntity registerUser(Long oauthId, KakaoPropertiesDto properties, HttpServletResponse response) {
		String profileImageUrl = properties.getProfile_image();
		String providerId = "KAKAO" + oauthId;
		Optional<UserEntity> optUser = userRepository.findByProviderId(providerId);
		String uuid;
		String refreshToken;
		UserEntity userEntity;

		if (optUser.isPresent()) {
			userEntity = optUser.get();
			RefreshToken foundRefreshToken = userService.findRefreshTokenByUserId(userEntity.getId());
			refreshToken = foundRefreshToken.getKeyValue();
			uuid = userEntity.getUuid();
		} else {
			uuid = userService.generateUUID();
			String tokenKey = jwtTokenUtil.createRefreshToken(uuid, List.of("USER"));

			RefreshToken createdRefreshToken = RefreshToken.builder()
				.keyValue(tokenKey)
				.build();

			UserEntity user = UserEntity
				.builder()
				.name(properties.getNickname())
				.roles("USER")
				.profileImg(profileImageUrl)
				.providerId(providerId)
				.uuid(uuid)
				.refreshToken(createdRefreshToken)
				.build();

			userEntity = userRepository.save(user);

			refreshToken = createdRefreshToken.getKeyValue();
		}
		String newAccessToken = jwtTokenUtil.createAccessToken(uuid, List.of("USER"));

		userService.setTokenInCookie(newAccessToken, refreshToken, response);

		return userEntity;
	}
}
