package com.ll.trip.domain.user.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.user.oauth.dto.KakaoPropertiesDto;
import com.ll.trip.domain.user.oauth.service.KakaoOAuth2Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user/oauth2")
public class OAuthController {

	private final KakaoOAuth2Service kakaoOAuth2Service;

	@GetMapping("/callback/kakao")
	public Mono<ResponseEntity<?>> handleOAuth2Callback(@RequestParam String code, HttpServletResponse response) {
		log.info("code = {}", code);

		return kakaoOAuth2Service.getToken(code)
			.flatMap(token -> {
				String accessToken = token.getAccess_token();
				log.info("accessToken : {}", accessToken);

				return kakaoOAuth2Service.getUserInfo(accessToken);
			})
			.map(userInfo -> {
				KakaoPropertiesDto properties = userInfo.getProperties();

				Long oauthId = userInfo.getId();
				String profileImageUrl = properties.getThumbnail_image();

				kakaoOAuth2Service.registerUser(oauthId, properties, response);
				log.info("name : {}", properties.getNickname());
				log.info("oauthId : {}", oauthId);
				log.info("profileImageUrl : {}", profileImageUrl);

				// 여기서 필요한 데이터를 포함하여 ResponseEntity를 구성합니다.
				// 예: userInfo 또는 properties를 기반으로 응답 구성
				return ResponseEntity.ok(userInfo);
			});
	}

}
