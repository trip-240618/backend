package com.ll.trip.global.security.userDetail;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class SecurityUser extends User implements OAuth2User {
	private final Long id;

	private final String uuid;

	private final String nickname;

	public SecurityUser(Long id, String uuid, String nickname,
		Collection<? extends GrantedAuthority> authorities) {
		super(uuid, "N/A", authorities); //username = uuid, password = providerId
		this.id = id;
		this.uuid = uuid;
		this.nickname = nickname;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Map.of(
			"id", id,
			"uuid", uuid,
			"nickname", nickname
		);
	}

	@Override
	public String getName() {
		return this.uuid;
	}
}
