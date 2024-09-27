package com.ll.trip.domain.user.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.ll.trip.global.security.service.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {

	@Value(value = "${jwt.token-secret}")
	private String tokenSecret;

	private Key key;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
	}

	@Value(value = "${jwt.access-token-time}")
	private long accessTokenValidityInMilliseconds;

	@Value(value = "${jwt.refresh-token-time}")
	private long refreshTokenValidityInMilliseconds;

	private final UserDetailsServiceImpl userDetailsService;

	public String createRefreshToken(String uuid, List<String> roles) {
		//TODO claim 최신화
		return getString(uuid, roles, refreshTokenValidityInMilliseconds);
	}

	public String createAccessToken(String uuid, List<String> roles) {

		return getString(uuid, roles, accessTokenValidityInMilliseconds);
	}

	private String getString(String uuid, List<String> roles, long tokenValidityInMilliseconds) {
		Claims claims = Jwts.claims().setSubject(uuid);
		claims.put("roles", roles);

		Date now = new Date();
		Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public Authentication getAuthentication(String token) {
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUuid(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUuid(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String token) {

		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.error("만료된 토큰: " + e.getMessage());
			return false;
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 토큰: " + e.getMessage());
			return false;
		} catch (MalformedJwtException e) {
			log.error("잘못된 구조의 토큰: " + e.getMessage());
			return false;
		} catch (SignatureException e) {
			log.error("서명이 유효하지 않은 토큰: " + e.getMessage());
			return false;
		} catch (IllegalArgumentException e) {
			log.error("잘못된 입력이나 null 값: " + e.getMessage());
			return false;
		}

	}

	public String resolveToken(HttpServletRequest req, String tokenName) {
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (tokenName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

}
