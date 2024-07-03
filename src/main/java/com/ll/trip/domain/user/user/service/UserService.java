package com.ll.trip.domain.user.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ll.trip.domain.user.user.dto.UserRegisterDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserEntity register(UserRegisterDto registerDto) {
		//TODO 예외처리

		UserEntity user = UserEntity.builder()
			.name(registerDto.getName())
			.providerId(registerDto.getProviderId())
			.password(passwordEncoder.encode(registerDto.getPassword()))
			.uuid(generateUUID())
			.roles("User")
			.profileImg(registerDto.getProfileImg())
			.build();

		return userRepository.save(user);
	}

	public String generateUUID() {
		return UUID.randomUUID().toString();
	}
}
