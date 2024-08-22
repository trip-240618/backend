package com.ll.trip.domain.user.user.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.dto.UserModifyDto;
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
		return refreshTokenRepository.findById(userId)
			.orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
	}

	public void setTokenInCookie(String accessToken, String refreshToken, HttpServletResponse response) {
		ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
			.httpOnly(true)
			.path("/")
			.secure(true)
			.sameSite("None")
			.build();

		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.path("/")
			.secure(true)
			.sameSite("None")
			.build();

		response.addHeader("Set-Cookie", accessTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookie.toString());
	}

	public UserInfoDto modifyUserInfo(UserEntity user, UserModifyDto modifyDto) {
		String nickname = modifyDto.getNickname();
		String profileImageUrl = modifyDto.getProfileImg();

		if (nickname != null)
			user.setNickname(nickname);

		if (profileImageUrl != null)
			user.setProfileImg(profileImageUrl);

		user = userRepository.save(user);

		return new UserInfoDto(user, "modify");
	}
}
