package com.ll.trip.domain.user.user.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.domain.notification.notification.repository.NotificationConfigRepository;
import com.ll.trip.global.security.filter.jwt.JwtTokenUtil;
import com.ll.trip.domain.user.user.entity.UserEntity;
import com.ll.trip.domain.user.user.repository.UserRepository;
import com.ll.trip.global.security.userDetail.SecurityUser;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final NotificationConfigRepository notificationConfigRepository;

	public Optional<UserEntity> findUserByUuid(String uuid) {
		return userRepository.findByUuid(uuid);
	}

	public String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public void createAndSetTokens(long userId, String uuid, String nickname,
		Collection<? extends GrantedAuthority> authorities, HttpServletResponse response) {
		String refreshToken = jwtTokenUtil.createRefreshToken(userId, uuid, nickname,
			authorities);
		String newAccessToken = jwtTokenUtil.createAccessToken(userId, uuid, nickname,
			authorities);
		setTokenInCookie(newAccessToken, refreshToken, response);
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
	public UserEntity modifyUserInfo(UserEntity userRef, String nickname, String profileImage, String thumbnail,
		String memo) {
		if (nickname != null)
			userRef.setNickname(nickname);

		userRef.setProfileImg(profileImage);
		userRef.setThumbnail(thumbnail);
		userRef.setMemo(memo);

		return userRepository.save(userRef);
	}

	public UserEntity findUserByUserId(long userId) {
		return userRepository.findById(userId).orElseThrow(NullPointerException::new);
	}

	@Transactional
	public int updateFcmTokenByUserId(long userId, String fcmToken) {
		return userRepository.updateFcmTokenByUserId(userId, fcmToken);
	}

	@Transactional
	public UserEntity registerUserInfo(UserEntity userRef, String nickname, String profileImg, String thumbnail,
		String memo, boolean marketing) {
		UserEntity user = modifyUserInfo(userRef, nickname, profileImg, thumbnail, memo);
		notificationConfigRepository.updateMarketingAgree(user.getId(), marketing);
		return user;
	}

	public boolean validateUser(SecurityUser securityUser) {
		UserEntity user = userRepository.findById(securityUser.getId()).orElseThrow(NullPointerException::new);
		return securityUser.getUuid().equals(user.getUuid())
			   && securityUser.getNickname().equals(user.getNickname())
			   && new HashSet<>(securityUser.getAuthorities()).equals(new HashSet<>(user.getAuthorities()));
	}

	@Transactional
	public void deleteUserById(long userId) {

		userRepository.deleteById(userId);
	}
}
