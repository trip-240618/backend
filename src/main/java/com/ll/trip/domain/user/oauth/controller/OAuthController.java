package com.ll.trip.domain.user.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.trip.domain.user.oauth.dto.AppleLoginRequestBody;
import com.ll.trip.domain.user.oauth.dto.GoogleLoginRequestBody;
import com.ll.trip.domain.user.oauth.dto.KakaoPropertiesDto;
import com.ll.trip.domain.user.oauth.service.KakaoOAuth2Service;
import com.ll.trip.domain.user.oauth.service.OAuth2Service;
import com.ll.trip.domain.user.user.dto.UserInfoDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user/oauth2")
public class OAuthController {
	private final KakaoOAuth2Service kakaoOAuth2Service;
	private final OAuth2Service oAuth2Service;

	@GetMapping("/callback/kakao")
	@Operation(summary = "카카오 로그인")
	@ApiResponse(responseCode = "200", description = "카카오 로그인 & 유저정보 반환", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public Mono<ResponseEntity<?>> handleOAuth2Callback(@RequestParam String token, HttpServletResponse response) {
		log.info("token = {}", token);
		//TODO code가 아니라 token을 사용하기 때문에 프론트와 함께 파라미터명을 수정해야함
		return kakaoOAuth2Service.getUserInfo(token)
			.publishOn(Schedulers.boundedElastic())
			.map(userInfo -> {
				KakaoPropertiesDto properties = userInfo.getProperties();

				String oauthId = userInfo.getId().toString();
				String name = properties.getNickname();
				String profileImageUrl = properties.getProfile_image();

				UserInfoDto userInfoDto = oAuth2Service.registerUser(oauthId, name, null, profileImageUrl, "KAKAO", response);
				log.info("name : {}", properties.getNickname());
				log.info("oauthId : {}", oauthId);
				log.info("profileImageUrl : {}", profileImageUrl);

				// 여기서 필요한 데이터를 포함하여 ResponseEntity를 구성합니다.
				// 예: userInfo 또는 properties를 기반으로 응답 구성
				return ResponseEntity.ok(userInfoDto);
			});
	}

	@PostMapping("/login/google")
	@Operation(summary = "구글 로그인")
	@ApiResponse(responseCode = "200", description = "구글 로그인 & 유저정보 반환", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public ResponseEntity<?> googleLogin(
		@RequestBody final GoogleLoginRequestBody requestBody,
		HttpServletResponse response) {
		String oauthId = requestBody.getId();
		String name = requestBody.getDisplayName();
		String profileImg = requestBody.getPhotoUrl();
		String email = requestBody.getEmail();

		UserInfoDto userInfoDto = oAuth2Service.registerUser(oauthId, name, email, profileImg, "GOOGLE", response);

		return ResponseEntity.ok(userInfoDto);
	}

	@PostMapping("/login/apple")
	@Operation(summary = "애플 로그인")
	@ApiResponse(responseCode = "200", description = "애플 로그인 & 유저정보 반환", content = {
		@Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoDto.class))})
	public ResponseEntity<?> appleLogin(@RequestBody final AppleLoginRequestBody requestBody,
		HttpServletResponse response) {
		String oauthId = requestBody.getUserIdentifier();
		String name = requestBody.getFamilyName() + requestBody.getGivenName();
		String profileImg = null;
		String email = requestBody.getEmail();

		UserInfoDto userInfoDto = oAuth2Service.registerUser(oauthId, name, email, profileImg, "APPLE", response);
		if (userInfoDto == null)
			return ResponseEntity.badRequest().body("userInfo is null");
		return ResponseEntity.ok(userInfoDto);
	}

}
