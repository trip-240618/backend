package com.ll.trip.domain.user.oauth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("user/oauth2")
public class OAuthTestController {
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId; // 카카오 클라이언트 ID

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String kakaoRedirectUri; // 리다이렉트 URI

	@GetMapping("/login/kakao")
	public String kakaoLogin() {
		String redirectUrl = String.format(
			"https://kauth.kakao.com/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=code", //&prompt=consent
			kakaoClientId, kakaoRedirectUri);
		return "redirect:" + redirectUrl;
	}

}
