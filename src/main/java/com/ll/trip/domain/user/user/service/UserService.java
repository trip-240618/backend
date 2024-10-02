package com.ll.trip.domain.user.user.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.notification.repository.NotificationConfigRepository;
import com.ll.trip.domain.user.user.dto.UserInfoDto;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final NotificationConfigRepository notificationConfigRepository;

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

	@Transactional
	public UserInfoDto modifyUserInfo(UserEntity user, String nickname, String profileImage, String thumbnail,
		String memo) {
		if (nickname != null)
			user.setNickname(nickname);

		user.setProfileImg(profileImage);
		user.setThumbnail(thumbnail);
		user.setMemo(memo);

		user = userRepository.save(user);

		return new UserInfoDto(user, "modify");
	}

	public UserEntity findUserByUserId(long userId) {
		return userRepository.findById(userId).orElseThrow(NullPointerException::new);
	}

	@Transactional
	public int updateFcmTokenByUserId(long userId, String fcmToken) {
		return userRepository.updateFcmTokenByUserId(userId, fcmToken);
	}

	@Transactional
	public UserInfoDto registerUserInfo(UserEntity user, String nickname, String profileImg, String thumbnail,
		String memo, boolean marketing) {
		UserInfoDto userInfoDto = modifyUserInfo(user, nickname, profileImg, thumbnail, memo);
		notificationConfigRepository.updateMarketingAgree(user.getId(), marketing);
		return userInfoDto;
	}
}
