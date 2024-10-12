package com.ll.trip.global.websocket.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.ll.trip.global.security.filter.jwt.JwtTokenUtil;
import com.ll.trip.global.security.filter.jwt.dto.ExtractedClaims;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtTokenUtil jwtTokenUtil;

	@Override
	public boolean beforeHandshake(
		ServerHttpRequest request,
		ServerHttpResponse response,
		WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		// JWT 토큰 추출
		// accessToken과 refreshToken을 가져옴
		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
		String accessToken = jwtTokenUtil.resolveToken(servletRequest, "accessToken");
		String refreshToken = jwtTokenUtil.resolveToken(servletRequest, "refreshToken");
		Claims claims = null;

		// 기존의 JWT 인증 로직 재사용
		boolean accessTokenValid = accessToken != null && jwtTokenUtil.validateToken(accessToken);
		if (accessTokenValid) {
			claims = jwtTokenUtil.getClaims(accessToken);
		} else if (refreshToken != null) {
			log.info("유효하지 않은 액세스토큰: " + accessToken);
			if (jwtTokenUtil.validateToken(refreshToken)) {
				claims = jwtTokenUtil.getClaims(refreshToken);
			} else {
				return false; // 인증 실패
			}
		} else {
			log.info("유효하지 않은 액세스토큰: " + accessToken);
			log.info("유효하지 않은 리프레시토큰: " + refreshToken);
			return false; // 인증 실패
		}

		// 인증 정보를 SecurityContext에 설정
		ExtractedClaims exClaims = new ExtractedClaims(claims);
		Authentication auth = jwtTokenUtil.buildAuthentication(
			exClaims.getUserId(),
			exClaims.getUuid(),
			exClaims.getNickname(),
			exClaims.getAuthorities()
		);
		SecurityContextHolder.getContext().setAuthentication(auth);

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {
		if (exception != null) {
			// 예외가 발생했을 경우 예외 로그를 남김
			log.error("WebSocket 핸드셰이크 중 오류 발생: ", exception);
		} else {
			// 핸드셰이크 성공 시 연결된 클라이언트의 정보를 로깅
			log.info("WebSocket 연결 성공: " + request.getRemoteAddress());
		}
	}

}
