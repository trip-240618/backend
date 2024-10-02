package com.ll.trip.global.security.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.global.security.userDetail.SecurityUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByUuid(uuid)
			.orElseThrow(() -> new UsernameNotFoundException("유저의 정보가 없습니다: " + uuid));

		return new SecurityUser(
			user.getId(),
			user.getUuid(),
			user.getNickname(),
			user.getAuthorities()
		);
	}

	public UserDetails buildUserByClaims(long id, String uuid, String nickname, Collection<? extends GrantedAuthority> authorities) {
		return new SecurityUser(id, uuid, nickname, authorities);
	}
}
