package com.ll.trip.domain.user.oauth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ll.trip.domain.user.oauth.dto.KakaoPropertiesDto;
import com.ll.trip.domain.user.oauth.service.KakaoOAuth2Service;
import com.ll.trip.domain.user.oauth.service.OAuth2Service;
import com.ll.trip.domain.user.user.dto.UserInfoDto;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("user/oauth2")
@Tag(name = "Test", description = "백엔드용 로그인 테스트 API")
public class OAuthTestController {
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId; // 카카오 클라이언트 ID

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String kakaoRedirectUri; // 리다이렉트 URI

	private final KakaoOAuth2Service kakaoOAuth2Service;
	private final OAuth2Service oAuth2Service;

	@GetMapping("/login/kakao")
	public String kakaoLogin() {
		String redirectUrl = String.format(
			"https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code", //&prompt=consent
			kakaoClientId, kakaoRedirectUri+"/test");
		return "redirect:" + redirectUrl;
	}

	@GetMapping("/callback/kakao/test")
	public Mono<ResponseEntity<?>> handleOAuth2Callback(@RequestParam String code, HttpServletResponse response) {
		log.info("code = {}", code);

		return kakaoOAuth2Service.getToken(code)
				.flatMap(token -> {
					String accessToken = token.getAccess_token();
					log.info("accessToken : {}", accessToken);

					return kakaoOAuth2Service.getUserInfo(accessToken);
				})
			.publishOn(Schedulers.boundedElastic())
			.map(userInfo -> {
				KakaoPropertiesDto properties = userInfo.getProperties();

				String oauthId = userInfo.getId().toString();
				String name = properties.getNickname();
				String profileImageUrl = properties.getProfile_image();

				UserInfoDto userInfoDto = oAuth2Service.whenLogin(oauthId, name, null, profileImageUrl, "KAKAO", null, response);
				log.info("name : {}", properties.getNickname());
				log.info("oauthId : {}", oauthId);
				log.info("profileImageUrl : {}", profileImageUrl);

				// 여기서 필요한 데이터를 포함하여 ResponseEntity를 구성합니다.
				// 예: userInfo 또는 properties를 기반으로 응답 구성
				return ResponseEntity.ok(userInfoDto);
			});
	}

}
