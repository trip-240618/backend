package com.ll.trip.global.websocket.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(
		ServerHttpRequest request,
		ServerHttpResponse response,
		WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		// SecurityContext에서 인증 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			log.warn("WebSocket 핸드셰이크에서 인증 정보를 찾을 수 없음");
			return false;
		}

		log.info("WebSocket에 연결중 uuid: " + authentication.getName());
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
