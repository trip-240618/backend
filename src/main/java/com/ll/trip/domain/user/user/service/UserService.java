package com.ll.trip.domain.user.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.dto.UserModifyDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public Optional<UserEntity> findUserByUuid(String uuid) {
		return userRepository.findByUuid(uuid);
	}

	public String generateUUID() {
		return UUID.randomUUID().toString();
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

		user.setProfileImg(profileImageUrl);
		user.setThumbnail(modifyDto.getThumbnail());
		user.setMemo(modifyDto.getMemo());

		user = userRepository.save(user);

		return new UserInfoDto(user, "modify");
	}

	public UserEntity findUserByUserId(long userId) {
		return userRepository.findById(userId).orElseThrow(NullPointerException::new);
	}

	public int updateFcmTokenByUserId(long userId, String fcmToken) {
		return userRepository.updateFcmTokenByUserId(userId, fcmToken);
	}
}
