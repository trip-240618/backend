package com.ll.trip.domain.user.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Value(value = "${jwt.token-secret}")
	private String tokenSecret;

	private final JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String accessToken = jwtTokenUtil.resolveToken(request, "accessToken");
		String refreshToken = jwtTokenUtil.resolveToken(request, "refreshToken");

		//액세스 토큰이 없음
		if (accessToken != null && jwtTokenUtil.validateToken(accessToken)) {
			//액세스 토큰이 유효함
			Authentication auth = jwtTokenUtil.getAuthentication(accessToken);
			SecurityContextHolder.getContext().setAuthentication(auth);
			filterChain.doFilter(request, response);
			return;
		}

		logger.info("유효하지 않은 액세스토큰: " + accessToken);

		//액세스토큰이 있으나 유효하지 않고 리프레시 토큰이 유효함
		if (refreshToken != null && jwtTokenUtil.validateToken(refreshToken)) {
			String uuid = jwtTokenUtil.getUuid(refreshToken);
			String newAccessToken = jwtTokenUtil.createAccessToken(uuid, List.of("USER"));
			ResponseCookie newAccessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
				.httpOnly(true)
				.path("/")
				.secure(true)
				.build();

			response.addHeader("Set-Cookie", newAccessTokenCookie.toString());

			Authentication auth = jwtTokenUtil.getAuthentication(newAccessToken);
			SecurityContextHolder.getContext().setAuthentication(auth);

			filterChain.doFilter(request, response);
			return;
		}

		logger.info("유효하지 않은 리프레시토큰: " + refreshToken);
		response.sendError(420, "유효하지 않은 리프레시토큰, 다시 로그인 하십시오.");
		filterChain.doFilter(request, response);
	}
}
