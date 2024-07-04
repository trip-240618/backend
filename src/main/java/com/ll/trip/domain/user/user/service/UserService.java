package com.ll.trip.domain.user.user.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ll.trip.domain.user.user.entity.RefreshToken;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.RefreshTokenRepository;
import com.ll.trip.domain.user.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;

	public Optional<UserEntity> findUserByUuid(String uuid) {
		return userRepository.findByUuid(uuid);
	}


	public String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public RefreshToken findRefreshTokenByUserId(Long userId) {
		return userRepository.findById(userId)
			.map(UserEntity::getRefreshToken)
			.orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
	}

	public void setTokenInCookie(String accessToken, String refreshToken, HttpServletResponse response) {
		ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.path("/")
			.secure(true)
			.build();

		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.path("/")
			.secure(true)
			.build();

		response.addHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());
	}
}
