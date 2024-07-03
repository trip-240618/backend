package com.ll.trip.global.security.config;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/hc")
					//예약,결제도 추가
					.authenticated()
					.anyRequest()
					.permitAll()
			)
			.csrf(
				csrf ->
					csrf.ignoringRequestMatchers(
						"**", //모든 post요청에 csrf토큰 심고나서 삭제
						"/oauth2/**" //소셜 로그인시 토큰과 사용자정보 받을 수 있도록
					)
			)
			.headers(
				headers ->
					headers
						.frameOptions(
							HeadersConfigurer.FrameOptionsConfig::sameOrigin
						)
			)
			.oauth2Login(
				oauth2Login ->
					oauth2Login
						.successHandler(customAuthenticationSuccessHandler)
			);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
