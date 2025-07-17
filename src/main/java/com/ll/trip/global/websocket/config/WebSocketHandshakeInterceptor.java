package com.ll.trip.global.websocket.config;

import com.ll.trip.global.security.filter.jwt.JwtTokenUtil;
import com.ll.trip.global.security.filter.jwt.dto.ExtractedClaims;
import com.ll.trip.global.security.userDetail.SecurityUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

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

		String accessToken = jwtTokenUtil.resolveWebSocketToken(request, "accessToken");
		String refreshToken = jwtTokenUtil.resolveWebSocketToken(request, "refreshToken");
		Claims claims = null;

		boolean accessTokenVaild = accessToken != null && jwtTokenUtil.validateToken(accessToken);
		if (accessTokenVaild) {
			claims = jwtTokenUtil.getClaims(accessToken);
		} else if (refreshToken != null) {
			log.info("유효하지 않은 액세스토큰: " + accessToken);
			if (jwtTokenUtil.validateToken(refreshToken)) {
				claims = jwtTokenUtil.getClaims(refreshToken);
			} else {
				log.info("유효하지 않은 리프레시토큰: " + refreshToken);
				return false;
			}
		} else {
			log.info("유효하지 않은 액세스토큰: " + accessToken);
			log.info("유효하지 않은 리프레시토큰: " + refreshToken);
			return false;
		}

		ExtractedClaims exClaims = new ExtractedClaims(claims);

		Authentication auth = jwtTokenUtil.buildAuthentication(exClaims.getUserId(), exClaims.getUuid(),
				exClaims.getNickname(), exClaims.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(auth);
		log.info("유저 인증 성공 : " + exClaims.getUserId());

		if (auth == null || !auth.isAuthenticated()) {
			log.warn("WebSocket 핸드셰이크에서 인증 정보를 찾을 수 없음");
			return false;
		}

		if (auth.getPrincipal() instanceof SecurityUser securityUser) {
			String nickname = securityUser.getNickname();
			if (nickname != null) {
				log.info("유저 웹소켓 연결 성공 : " + nickname);
				attributes.put("nickname", nickname);
			} else {
				log.warn("WebSocket 핸드셰이크 중 닉네임이 null입니다.");
				return false;
			}
		} else {
			log.warn("WebSocket 핸드셰이크에서 예상치 못한 Principal 타입");
			return false;
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {
		if (exception != null) {
			// 예외가 발생했을 경우 예외 로그를 남김
			log.error("WebSocket 핸드셰이크 중 오류 발생: ", exception);
		}
	}

}
