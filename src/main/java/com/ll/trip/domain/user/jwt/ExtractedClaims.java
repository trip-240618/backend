package com.ll.trip.domain.user.jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import lombok.Getter;

@Getter
public class ExtractedClaims {
	private final long userId;
	private final String uuid;
	private final String nickname;
	private final Collection<? extends GrantedAuthority> authorities;

	public ExtractedClaims(Claims claims) {
		this.uuid = claims.getSubject();  // UUID 추출
		this.userId = ((Number)claims.get("id")).longValue();
		this.nickname = claims.get("nickname", String.class);
		List<String> roles = claims.get("roles", List.class);
		this.authorities = roles.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}
}
